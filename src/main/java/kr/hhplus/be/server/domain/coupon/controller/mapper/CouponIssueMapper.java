package kr.hhplus.be.server.domain.coupon.controller.mapper;

import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import kr.hhplus.be.server.domain.coupon.controller.dto.CouponIssueResponse;
import org.springframework.stereotype.Component;

@Component
public class CouponIssueMapper {
    public CouponIssueResponse toResponse(CouponIssue d) {
        return new CouponIssueResponse(
                d.getCouponIssueId(),
                d.getCouponCode(),
                d.getCouponId(),
                d.getUpdateDt().toString(),
                d.getExpireDate().toString()
        );
    }
}
