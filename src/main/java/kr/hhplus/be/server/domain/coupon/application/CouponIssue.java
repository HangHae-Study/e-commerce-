package kr.hhplus.be.server.domain.coupon.application;

import kr.hhplus.be.server.common.optimistic.VersionedDomain;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Data
@SuperBuilder
public class CouponIssue extends VersionedDomain {

    private Long couponIssueId;

    private String couponCode;
    private Long couponId;
    private Long userId;
    private String couponValid;
    private BigDecimal discountRate;
    private LocalDateTime expireDate;
    private LocalDateTime updateDt;

    public boolean isValid(){
        return expireDate.isAfter(LocalDateTime.now()) && couponValid.equals("Y");
    }

    public CouponIssue used(){
        if (isValid()) {
            this.couponValid = "N";
            return this;
        }else{
            throw new IllegalStateException("이미 사용된 쿠폰입니다");
        }
    }

    public BigDecimal getDiscountRateRatio(){
        return discountRate.divide(BigDecimal.valueOf(100L)).setScale(0, RoundingMode.HALF_UP);
    }

    public static CouponIssue issueNew(Coupon coupon, Long userId, String couponCode) {
        return CouponIssue.builder()
                .couponCode(couponCode)
                .couponId(coupon.getCouponId())
                .userId(userId)
                .couponValid("Y")
                .discountRate(coupon.getDiscountRate())
                .expireDate(coupon.getExpireDate())
                .updateDt(LocalDateTime.now())
                .build();
    }

}
