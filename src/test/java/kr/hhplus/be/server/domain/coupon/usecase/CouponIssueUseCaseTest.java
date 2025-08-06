package kr.hhplus.be.server.domain.coupon.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.coupon.adapter.entity.CouponJpaEntity;
import kr.hhplus.be.server.domain.coupon.application.Coupon;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import kr.hhplus.be.server.domain.coupon.application.generator.CouponCodeGenerator;
import kr.hhplus.be.server.domain.coupon.application.repository.CouponIssueRepository;
import kr.hhplus.be.server.domain.coupon.application.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.application.service.CouponService;
import kr.hhplus.be.server.domain.coupon.controller.CouponController;
import kr.hhplus.be.server.domain.coupon.controller.dto.CouponIssueRequest;
import kr.hhplus.be.server.domain.coupon.controller.dto.CouponIssueResponse;
import kr.hhplus.be.server.domain.coupon.controller.mapper.CouponIssueMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CouponIssueUseCaseTest {
    @Nested
    @DisplayName("쿠폰 도메인 발급 책임")
    @ExtendWith(MockitoExtension.class)
    class CouponIssueUseCaseDomainTest{
        @Mock
        CouponCodeGenerator codeGenerator;  // 쿠폰 코드 생성기

        @Test
        void 쿠폰_도메인_발급() {
            // given
            Coupon coupon = Coupon.builder()
                    .couponId(1L)
                    .remaining(5L)
                    .discountRate(new BigDecimal("10"))
                    .expireDate(LocalDateTime.now().plusDays(7))
                    .build();
            given(codeGenerator.generate(coupon, 42L, coupon.getRemaining())).willReturn("ABC-123");

            // when
            CouponIssue issue = coupon.issueTo(42L, codeGenerator);

            // then: 남은 수량이 1 줄어든다
            assertThat(coupon.getRemaining()).isEqualTo(4L);
            // 생성된 CouponIssue 필드 검증
            assertThat(issue.getUserId()).isEqualTo(42L);
            assertThat(issue.getCouponId()).isEqualTo(1L);
            assertThat(issue.getCouponCode()).isEqualTo("ABC-123");
            assertThat(issue.getExpireDate()).isEqualTo(coupon.getExpireDate());
        }

        @Test
        void 쿠폰_도메인_발급_실패() {
            Coupon coupon = Coupon.builder()
                    .couponId(1L)
                    .remaining(0L)
                    .discountRate(new BigDecimal("10"))
                    .expireDate(LocalDateTime.now().plusDays(7))
                    .build();

            assertThatThrownBy(() -> coupon.issueTo(42L, codeGenerator))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("쿠폰 발급에 실패하였습니다.");
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    class CouponServiceTest {

        @Mock
        CouponRepository couponRepository;
        @Mock
        CouponIssueRepository couponIssueRepository;
        @Mock CouponCodeGenerator codeGenerator;

        @InjectMocks
        CouponService couponService;  // @Service 클래스

        @Test
        void 신규_쿠폰_발급_성공() {
            // given
            Coupon coupon = Coupon.builder()
                    .couponId(10L)
                    .remaining(2L)
                    .discountRate(new BigDecimal("15"))
                    .expireDate(LocalDateTime.now().plusDays(10))
                    .build();

            given(couponRepository.findById(10L))
                    .willReturn(Optional.of(coupon));
            given(codeGenerator.generate(coupon, 99L, coupon.getRemaining())).willReturn("CODE-XYZ");
            given(couponRepository.save(any(Coupon.class))).willAnswer(inv -> inv.getArgument(0));
            given(couponIssueRepository.save(any(CouponIssue.class)))
                    .willAnswer(inv -> {
                        CouponIssue ci = inv.getArgument(0);
                        return CouponIssue.builder()
                                .couponIssueId(100L)
                                .couponCode(ci.getCouponCode())
                                .userId(ci.getUserId())
                                .couponId(ci.getCouponId())
                                .discountRate(ci.getDiscountRate())
                                .expireDate(ci.getExpireDate())
                                .updateDt(ci.getUpdateDt())
                                .build();
                    });

            // when
            CouponIssue result = couponService.newCouponIssue(99L, 10L);

            // then
            assertThat(result.getCouponIssueId()).isEqualTo(100L);
            assertThat(result.getCouponCode()).isEqualTo("CODE-XYZ");
            // coupon 의 remaining 이 1 줄어들었는지
            verify(couponRepository).save(argThat(c -> c.getRemaining() == 1L));
            verify(couponIssueRepository).save(any());
        }

        @Test
        void 신규_쿠폰_발급_실패_유효하지않은쿠폰() {
            given(couponRepository.findById(50L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> couponService.newCouponIssue(1L, 50L))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("존재하지 않는 쿠폰입니다.");
        }

        @Test
        void 신규_쿠폰_발급_실패_쿠폰수량부족() {
            Coupon coupon = Coupon.builder()
                    .couponId(10L)
                    .remaining(0L)
                    .discountRate(new BigDecimal("15"))
                    .expireDate(LocalDateTime.now().plusDays(10))
                    .build();
            given(couponRepository.findById(10L)).willReturn(Optional.of(coupon));

            assertThatThrownBy(() -> couponService.newCouponIssue(1L, 10L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("쿠폰 발급에 실패하였습니다.");
        }
    }

    @Nested
    @DisplayName("쿠폰 컨트롤러 테스트")
    @AutoConfigureMockMvc
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    class CouponControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private CouponRepository couponRepository;

        private Long savedCouponId;
        @BeforeEach
        void setup(){
            // 테스트용 쿠폰 한 건 저장
            Coupon domain = Coupon.builder()
                    .totalIssued(0L)
                    .remaining(100L)
                    .discountRate(new BigDecimal("20"))
                    .expireDate(LocalDateTime.now().plusDays(1))
                    .updateDt(LocalDateTime.now())
                    .build();

            CouponJpaEntity saved = CouponJpaEntity.fromDomain(couponRepository.save(domain));
            savedCouponId = saved.getCouponId();
        }


        @Test
        @DisplayName("POST /coupons => 200")
        void 쿠폰_발급_요청_API() throws Exception {
            // given
            long userId = 1L;
            CouponIssueRequest req = new CouponIssueRequest(userId, savedCouponId);

            // when / then
            mockMvc.perform(post("/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.couponIssueId").isNotEmpty())
                    .andExpect(jsonPath("$.data.couponCode").isNotEmpty())
                    .andExpect(jsonPath("$.data.couponId").value(savedCouponId));
        }

        @Test
        @DisplayName("POST /coupons => 404")
        void 쿠폰_발급_요청_API_실패() throws Exception {
            // given
            long userId = 1L;
            long couponId = 999L;
            CouponIssueRequest req = new CouponIssueRequest(userId, couponId);
            // when / then
            mockMvc.perform(post("/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("존재하지 않는 쿠폰입니다."));
        }
    }

}
