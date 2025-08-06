package kr.hhplus.be.server.domain.coupon.application.repository;

import kr.hhplus.be.server.common.RepositoryPort;
import kr.hhplus.be.server.domain.coupon.application.Coupon;

import java.time.LocalDateTime;
import java.util.List;

public interface CouponRepository extends RepositoryPort<Long, Coupon> {
    List<Coupon> findValidCouponList(LocalDateTime time, Long rem);
}
