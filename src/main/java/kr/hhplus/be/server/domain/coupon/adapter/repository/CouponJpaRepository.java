package kr.hhplus.be.server.domain.coupon.adapter.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.coupon.adapter.entity.CouponJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponJpaRepository extends JpaRepository<CouponJpaEntity, Long> {
    List<CouponJpaEntity> findCouponJpaEntitiesByExpireDateIsGreaterThanEqualAndRemainingIsGreaterThan(LocalDateTime stndDate, Long zero);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CouponJpaEntity c WHERE c.couponId = :cId")
    Optional<CouponJpaEntity> findByIdForUpdate(@Param("cId") Long cId);
}
