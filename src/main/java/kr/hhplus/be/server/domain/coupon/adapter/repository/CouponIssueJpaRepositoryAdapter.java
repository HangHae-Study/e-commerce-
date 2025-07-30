package kr.hhplus.be.server.domain.coupon.adapter.repository;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.adapter.entity.CouponIssueJpaEntity;
import kr.hhplus.be.server.domain.coupon.adapter.entity.CouponJpaEntity;
import kr.hhplus.be.server.domain.coupon.application.Coupon;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import kr.hhplus.be.server.domain.coupon.application.repository.CouponIssueRepository;
import kr.hhplus.be.server.domain.coupon.application.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponIssueJpaRepositoryAdapter implements CouponIssueRepository {

    private final CouponIssueJpaRepository couponIssueJpaRepository;

    @Override
    public Optional<CouponIssue> findById(Long ciId) {
        return couponIssueJpaRepository.findById(ciId).map(CouponIssueJpaEntity::toDomain);
    }

    @Override
    public List<CouponIssue> findAll() {
        return null;
    }

    @Override
    @Transactional
    public CouponIssue save(CouponIssue couponIssue) {
        CouponIssueJpaEntity entity = couponIssueJpaRepository.save(CouponIssueJpaEntity.fromDomain(couponIssue));
        return entity.toDomain();
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public Optional<CouponIssue> findByCouponCode(String code) {
        return couponIssueJpaRepository.findCouponIssueJpaEntitiesByCouponCode(code).map(CouponIssueJpaEntity::toDomain);

    }
}
