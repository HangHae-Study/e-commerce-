package kr.hhplus.be.server.domain.coupon.application.repository;

import kr.hhplus.be.server.common.RepositoryPort;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;

import java.util.Optional;

public interface CouponIssueRepository extends RepositoryPort<Long, CouponIssue> {
    public Optional<CouponIssue> findByCouponCode(String code);
}
