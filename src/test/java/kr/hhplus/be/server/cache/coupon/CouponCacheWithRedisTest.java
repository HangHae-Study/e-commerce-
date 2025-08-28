package kr.hhplus.be.server.cache.coupon;

import kr.hhplus.be.server.TestDataSourceProxyConfig;
import kr.hhplus.be.server.TestcontainersConfiguration;
import kr.hhplus.be.server.domain.coupon.adapter.cache.CouponCacheKeyProvider;
import kr.hhplus.be.server.domain.coupon.adapter.cache.CouponCacheQueue;
import kr.hhplus.be.server.domain.coupon.adapter.cache.CouponCacheRepository;
import kr.hhplus.be.server.domain.coupon.adapter.cache.CouponIssuedCacheRepository;
import kr.hhplus.be.server.domain.coupon.application.Coupon;
import kr.hhplus.be.server.domain.coupon.application.service.CouponService;
import kr.hhplus.be.server.domain.coupon.controller.dto.CouponClaimCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.server.domain.coupon.adapter.cache.CouponCacheKeyProvider.CouponClaimStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

@Import({
        TestcontainersConfiguration.class
})
@SpringBootTest
public class CouponCacheWithRedisTest {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    CouponCacheRepository couponCacheRepository;

    @Autowired
    CouponIssuedCacheRepository  couponIssuedCacheRepository;

    @Autowired
    CouponCacheQueue couponCacheQueue;

    @Autowired
    CouponService couponService;

    Coupon coupon = Coupon.builder()
            .couponId(1L)
            .totalIssued(100L)
            .remaining(100L)
            .discountRate(BigDecimal.valueOf(20L))
            .expireDate(LocalDateTime.now().plusDays(90))
            .updateDt(LocalDateTime.now())
            .build();

    @Nested
    class InitCoupon{
        @Test
        void 쿠폰_메타데이터_초기_캐싱_개수넣기(){

            couponCacheRepository.cachingCoupon(coupon);

            Coupon cached = couponCacheRepository.getCoupon(1L);
            Long couponStock = couponCacheRepository.getRemainingStock(1L);

            Assertions.assertNotNull(cached);
            assertThat(cached.getCouponId()).isEqualTo(1L);

            assertThat(couponStock).isEqualTo(coupon.getTotalIssued());

            //System.out.println(cached);
            //System.out.println(couponStock);
        }

        @Test
        void 쿠폰_재고수량_원자_감소_확인(){
            couponCacheRepository.cachingCoupon(coupon);
            couponCacheRepository.tryConsumeStock(coupon.getCouponId());

            Coupon cached = couponCacheRepository.getCoupon(1L);
            Long couponStock = couponCacheRepository.getRemainingStock(1L);


            Assertions.assertNotNull(cached);
            assertThat(cached.getCouponId()).isEqualTo(1L);

            assertThat(couponStock).isNotEqualTo(coupon.getTotalIssued());
        }
    }


    @Nested
    class CouponQueue{

        @Test
        void 쿠폰_요청시_큐_등록_확인(){
            boolean a = couponCacheQueue.enqueueClaim(1L, 1L);
            couponCacheQueue.enqueueClaim(1L, 2L);
            couponCacheQueue.enqueueClaim(1L, 3L);

            Long queueSize = couponCacheQueue.getQueueSize(1L);
            Long rank1 = couponCacheQueue.getRank(1L, 1L);
            Long rank2 = couponCacheQueue.getRank(1L, 2L);
            Long rank3 = couponCacheQueue.getRank(1L, 3L);


            assertThat(a).isTrue();
            assertThat(queueSize).isEqualTo(3L);
            assertThat(rank3).isEqualTo(3L);

            /*
            System.out.println(rank1);
            System.out.println(rank2);
            System.out.println(rank3);
             */

            List<String> pop = couponCacheQueue.popOldestMembers(1L, 1L);
            System.out.println(pop);
            System.out.println(couponCacheQueue.getQueueSize(1L));
        }
    }

    @Nested
    class CouponClaim{
        @Test
        void 쿠폰_요청시_큐_등록_후_상태(){
            boolean a = couponCacheQueue.enqueueClaim(1L, 1L);
            Long queueSize = couponCacheQueue.getQueueSize(1L);

            assertThat(a).isTrue();
            assertThat(queueSize).isEqualTo(1L);

            CouponCacheKeyProvider.CouponClaimStatus status = couponIssuedCacheRepository.getClaimStatus(1L, 1L).get();

            System.out.println(status);

            assertThat(status).isEqualTo(WAITED);
        }

        @Test
        void 쿠폰_요청_후_발급_시_상태(){
            boolean a = couponCacheQueue.enqueueClaim(1L, 1L);
            Long queueSize = couponCacheQueue.getQueueSize(1L);

            assertThat(a).isTrue();
            assertThat(queueSize).isEqualTo(1L);

            List<String> pop = couponCacheQueue.popOldestMembers(1L, 1L);
            couponIssuedCacheRepository.finalizeClaimStatus(1L, Long.valueOf(pop.get(0)), Duration.ofMinutes(30), null);
            CouponCacheKeyProvider.CouponClaimStatus status = couponIssuedCacheRepository.getClaimStatus(1L, 1L).get();

            System.out.println(status);

            assertThat(status).isEqualTo(ISSUED);
        }

        @Test
        void 쿠폰_서비스_클레임_1번째_요청_쿠폰발급(){
            couponCacheRepository.cachingCoupon(coupon);
            CouponClaimCommand.CouponClaimResponse claim = couponService.couponClaim(1L, 1L, "");

            couponCacheQueue.getQueueSize(1L);

            CouponCacheKeyProvider.CouponClaimStatus status = couponIssuedCacheRepository.getClaimStatus(1L, 1L).get();
            System.out.println(status);

            List<String> pop = couponCacheQueue.popOldestMembers(1L, 1L);
            assertThat(pop.size()).isEqualTo(1);


            System.out.println(claim);
            System.out.println(pop);
        }
    }

}
