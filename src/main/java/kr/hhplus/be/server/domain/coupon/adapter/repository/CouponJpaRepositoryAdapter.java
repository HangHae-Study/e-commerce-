package kr.hhplus.be.server.domain.coupon.adapter.repository;

import kr.hhplus.be.server.domain.coupon.adapter.entity.CouponJpaEntity;
import kr.hhplus.be.server.domain.coupon.application.Coupon;
import kr.hhplus.be.server.domain.coupon.application.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
        return null;
    }

    @Override
    public Coupon save(Coupon coupon) {
        couponJpaRepository.save(CouponJpaEntity.fromDomain(coupon));
        return null;
    }

    @Override
    public void deleteById(Long cId) {

    }
}
