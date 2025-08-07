package kr.hhplus.be.server.domain.coupon.adapter.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.coupon.adapter.entity.CouponIssueJpaEntity;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponIssueJpaRepository extends JpaRepository<CouponIssueJpaEntity, Long> {

    public Optional<CouponIssueJpaEntity> findCouponIssueJpaEntitiesByCouponCode(String code);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT c FROM CouponIssueJpaEntity c WHERE c.couponCode = :code")
    Optional<CouponIssueJpaEntity> findByCouponCodeWithLock(@Param("code") String code);
}
