package kr.hhplus.be.server.common.init;

import kr.hhplus.be.server.domain.coupon.adapter.cache.CouponCacheRepository;
import kr.hhplus.be.server.domain.coupon.application.Coupon;
import kr.hhplus.be.server.domain.product.application.repository.ProductLineRepository;
import kr.hhplus.be.server.domain.product.application.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner
{
    private final CouponCacheRepository couponCacheRepository;

    @Override
    public void run(String... args) {
        couponCacheInitialize();
    }


    private void couponCacheInitialize() {
        // 쿠폰1: 100장, 20% 할인
        Coupon coupon1 = Coupon.builder()
                .couponId(1L)
                .totalIssued(100L)
                .remaining(100L)
                .discountRate(BigDecimal.valueOf(20))
                .expireDate(LocalDateTime.now().plusDays(90))
                .updateDt(LocalDateTime.now())
                .build();
        couponCacheRepository.cachingCoupon(coupon1);

        // 쿠폰2: 200장, 15% 할인
        Coupon coupon2 = Coupon.builder()
                .couponId(2L)
                .totalIssued(200L)
                .remaining(200L)
                .discountRate(BigDecimal.valueOf(15))
                .expireDate(LocalDateTime.now().plusDays(60))
                .updateDt(LocalDateTime.now())
                .build();
        couponCacheRepository.cachingCoupon(coupon2);

        // 쿠폰3: 50장, 30% 할인
        Coupon coupon3 = Coupon.builder()
                .couponId(3L)
                .totalIssued(50L)
                .remaining(50L)
                .discountRate(BigDecimal.valueOf(30))
                .expireDate(LocalDateTime.now().plusDays(30))
                .updateDt(LocalDateTime.now())
                .build();
        couponCacheRepository.cachingCoupon(coupon3);
    }


}
