package kr.hhplus.be.server.domain.coupon.usecase;

import kr.hhplus.be.server.domain.coupon.application.Coupon;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import kr.hhplus.be.server.domain.coupon.application.generator.CouponCodeGenerator;
import kr.hhplus.be.server.domain.coupon.application.repository.CouponIssueRepository;
import kr.hhplus.be.server.domain.coupon.application.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.application.service.CouponService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

public class CouponIssueUseCaseTest {
    @Nested
    @DisplayName("쿠폰 도메인 발급 책임")
    @ExtendWith(MockitoExtension.class)
    class CouponIssueUseCaseDomainTest{
        @Mock
        CouponCodeGenerator codeGenerator;  // 쿠폰 코드 생성기

        @Test
        @DisplayName("남은 수량이 있으면 issueTo 시 remaining 이 감소하고 CouponIssue 생성")
        void issueTo_success() {
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
        @DisplayName("남은 수량이 0 이면 issueTo 시 예외 발생")
        void issueTo_noRemaining_throws() {
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
        @DisplayName("newCouponIssue 성공 시 Coupon 과 CouponIssue 가 저장되고 반환된다")
        void newCouponIssue_success() {
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
        @DisplayName("존재하지 않는 쿠폰 ID 로 조회 시 NoSuchElementException")
        void newCouponIssue_notFound() {
            given(couponRepository.findById(50L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> couponService.newCouponIssue(1L, 50L))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("존재하지 않는 쿠폰입니다.");
        }

        @Test
        @DisplayName("남은 수량 없는 쿠폰 발급 시 IllegalStateException")
        void newCouponIssue_noRemaining() {
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

}
