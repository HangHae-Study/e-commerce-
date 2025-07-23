package kr.hhplus.be.server.layered;

import kr.hhplus.be.server.domain.coupon.adapter.entity.CouponIssueJpaEntity;
import kr.hhplus.be.server.domain.coupon.adapter.entity.CouponJpaEntity;
import kr.hhplus.be.server.domain.coupon.adapter.repository.CouponIssueJpaRepository;
import kr.hhplus.be.server.domain.coupon.adapter.repository.CouponJpaRepository;
import kr.hhplus.be.server.domain.coupon.application.Coupon;
import kr.hhplus.be.server.domain.coupon.application.repository.CouponIssueRepository;
import kr.hhplus.be.server.domain.coupon.application.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponIssueRepository couponIssueRepository;

    private Long couponId;
    private Long userId;

    @BeforeEach
    void setUp() {
        // 1) 테스트용 쿠폰을 미리 저장
        Coupon couponDomain = Coupon.builder()
                .totalIssued(5L)
                .remaining(5L)
                .discountRate(new BigDecimal("0.20"))
                .expireDate(LocalDateTime.now().plusDays(7))
                .updateDt(LocalDateTime.now())
                .build();

        Coupon couponEntity = couponRepository.save(
                couponDomain
        );
        couponId = couponEntity.getCouponId();
        userId = 1L;
    }

    @Test
    void issueCoupon_success() throws Exception {
        // 2) 발급 요청
        String requestJson = String.format("{\"userId\": %d , \"couponId\":%d}", userId, couponId);

        mockMvc.perform(post("/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.couponIssueId").isNumber())
                .andExpect(jsonPath("$.data.couponId").value(couponId));
    }
}
