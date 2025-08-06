package kr.hhplus.be.server.domain.coupon.adapter.repository;

import kr.hhplus.be.server.domain.coupon.adapter.entity.CouponJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CouponJpaRepository extends JpaRepository<CouponJpaEntity, Long> {
    List<CouponJpaEntity> findCouponJpaEntitiesByExpireDateIsGreaterThanEqualAndRemainingIsGreaterThan(LocalDateTime stndDate, Long zero);
}
