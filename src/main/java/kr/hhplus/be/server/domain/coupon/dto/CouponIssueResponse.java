package kr.hhplus.be.server.domain.coupon.dto;

public record CouponIssueResponse(
        Long couponIssueId,
        String couponCode,
        Long couponId,
        String issueDt,
        String expireDt
        )
{}