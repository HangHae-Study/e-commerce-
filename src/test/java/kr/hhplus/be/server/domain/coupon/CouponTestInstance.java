package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.application.Coupon;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponTestInstance {
    /** ID 없이, 남은 쿠폰 1개인 기본 Coupon 생성 */
    public static Coupon simpleCoupon() {
        return Coupon.builder()
                .totalIssued(10L)
                .remaining(1L)
                .discountRate(new BigDecimal("10"))
                .expireDate(LocalDateTime.now().plusDays(7))
                .updateDt(LocalDateTime.now())
                .build();
    }

    /** 남은 수량이 0인 Coupon 생성 */
    public static Coupon couponWithNoRemaining() {
        return Coupon.builder()
                .totalIssued(10L)
                .remaining(0L)
                .discountRate(new BigDecimal("20"))
                .expireDate(LocalDateTime.now().plusDays(7))
                .updateDt(LocalDateTime.now())
                .build();
    }

    /** ID 포함, 특정 remaining으로 Coupon 생성 */
    public static Coupon persistedCoupon(Long id, Long remaining) {
        return Coupon.builder()
                .couponId(id)
                .totalIssued(10L)
                .remaining(remaining)
                .discountRate(new BigDecimal("15"))
                .expireDate(LocalDateTime.now().plusDays(7))
                .updateDt(LocalDateTime.now())
                .build();
    }

    public static Coupon.CouponBuilder simpleCouponBuilder() {
        return Coupon.builder()
                .totalIssued(10L)
                .remaining(10L)
                .discountRate(new BigDecimal("0.1"))
                .updateDt(LocalDateTime.now());
    }

}
