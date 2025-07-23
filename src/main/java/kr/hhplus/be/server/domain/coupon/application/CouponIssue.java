package kr.hhplus.be.server.domain.coupon.application;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CouponIssue {

    private Long couponIssueId;

    private String couponCode;
    private Long couponId;
    private Long userId;
    private String couponValid;
    private BigDecimal discountRate;
    private LocalDateTime expireDate;
    private LocalDateTime updateDt;
}
