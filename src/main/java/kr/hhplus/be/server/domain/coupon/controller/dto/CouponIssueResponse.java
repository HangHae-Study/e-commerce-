package kr.hhplus.be.server.domain.coupon.controller.dto;

public record CouponIssueResponse(
        Long couponIssueId,
        String couponCode,
        Long couponId,
        String issueDt,
        String expireDt
        )
{}