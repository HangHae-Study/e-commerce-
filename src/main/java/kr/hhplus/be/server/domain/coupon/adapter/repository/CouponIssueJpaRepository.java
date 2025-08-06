package kr.hhplus.be.server.domain.coupon.adapter.repository;

import kr.hhplus.be.server.domain.coupon.adapter.entity.CouponIssueJpaEntity;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponIssueJpaRepository extends JpaRepository<CouponIssueJpaEntity, Long> {

    public Optional<CouponIssueJpaEntity> findCouponIssueJpaEntitiesByCouponCode(String code);
}
