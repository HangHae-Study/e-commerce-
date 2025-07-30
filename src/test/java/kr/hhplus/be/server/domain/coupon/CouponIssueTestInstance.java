package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.application.CouponIssue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponIssueTestInstance {

    /** 유효한 CouponIssue 생성 */
    public static CouponIssue simpleValidIssue() {
        return CouponIssue.builder()
                .couponCode("CODE123")
                .couponId(1L)
                .userId(100L)
                .couponValid("Y")
                .discountRate(new BigDecimal("10"))
                .expireDate(LocalDateTime.now().plusDays(1))
                .updateDt(LocalDateTime.now())
                .build();
    }

    /** 이미 만료된 CouponIssue 생성 */
    public static CouponIssue expiredIssue() {
        return CouponIssue.builder()
                .couponCode("EXPIRED")
                .couponId(2L)
                .userId(200L)
                .couponValid("Y")
                .discountRate(new BigDecimal("05"))
                .expireDate(LocalDateTime.now().minusDays(1))
                .updateDt(LocalDateTime.now())
                .build();
    }

    /** 이미 사용 처리된 CouponIssue 생성 */
    public static CouponIssue usedIssue() {
        return CouponIssue.builder()
                .couponCode("USED")
                .couponId(3L)
                .userId(300L)
                .couponValid("N")
                .discountRate(new BigDecimal("20"))
                .expireDate(LocalDateTime.now().plusDays(5))
                .updateDt(LocalDateTime.now())
                .build();
    }

    /** ID 포함 CouponIssue 생성 */
    public static CouponIssue persistedIssue(Long issueId, String code) {
        return CouponIssue.builder()
                .couponIssueId(issueId)
                .couponCode(code)
                .couponId(4L)
                .userId(400L)
                .couponValid("Y")
                .discountRate(new BigDecimal("25"))
                .expireDate(LocalDateTime.now().plusDays(10))
                .updateDt(LocalDateTime.now())
                .build();
    }
}
