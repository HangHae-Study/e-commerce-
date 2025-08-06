package kr.hhplus.be.server.domain.coupon.adapter.repository;

import kr.hhplus.be.server.domain.coupon.adapter.entity.CouponJpaEntity;
import kr.hhplus.be.server.domain.coupon.application.Coupon;
import kr.hhplus.be.server.domain.coupon.application.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponJpaRepositoryAdapter implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;

    @Override
    public Optional<Coupon> findById(Long cId) {
        return couponJpaRepository.findById(cId).map(CouponJpaEntity::toDomain);
    }

    @Override
    public List<Coupon> findAll() {
        return couponJpaRepository.findAll().stream().map(CouponJpaEntity::toDomain).toList();
    }

    @Override
    public List<Coupon> findValidCouponList(LocalDateTime time, Long rem){
        return couponJpaRepository.findCouponJpaEntitiesByExpireDateIsGreaterThanEqualAndRemainingIsGreaterThan(
                time, rem).stream().map(
                        CouponJpaEntity::toDomain
        ).toList();
    }

    @Override
    public Coupon save(Coupon coupon) {
        CouponJpaEntity saved = couponJpaRepository.save(CouponJpaEntity.fromDomain(coupon));
        return saved.toDomain();
    }

    @Override
    public void deleteById(Long cId) {

    }
}
