package kr.hhplus.be.server.domain.coupon.application.repository;

import kr.hhplus.be.server.common.RepositoryPort;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;

public interface CouponIssueRepository extends RepositoryPort<Long, CouponIssue> {
    public CouponIssue findByCouponCode(String code);
}
