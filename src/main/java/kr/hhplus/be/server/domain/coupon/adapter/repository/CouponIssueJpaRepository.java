package kr.hhplus.be.server.domain.coupon.adapter.repository;

import kr.hhplus.be.server.domain.coupon.adapter.entity.CouponIssueJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponIssueJpaRepository extends JpaRepository<CouponIssueJpaEntity, Long> {
}
