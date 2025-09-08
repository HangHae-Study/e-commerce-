package kr.hhplus.be.server.cache.coupon;

import kr.hhplus.be.server.TestDataSourceProxyConfig;
import kr.hhplus.be.server.TestcontainersConfiguration;
import kr.hhplus.be.server.domain.coupon.adapter.cache.CouponCacheKeyProvider;
import kr.hhplus.be.server.domain.coupon.adapter.cache.CouponCacheQueue;
import kr.hhplus.be.server.domain.coupon.adapter.cache.CouponCacheRepository;
import kr.hhplus.be.server.domain.coupon.adapter.cache.CouponIssuedCacheRepository;
import kr.hhplus.be.server.domain.coupon.application.Coupon;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import kr.hhplus.be.server.domain.coupon.application.repository.CouponIssueRepository;
import kr.hhplus.be.server.domain.coupon.application.service.CouponService;
import kr.hhplus.be.server.domain.coupon.controller.dto.CouponClaimCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    @Autowired
    CouponIssueRepository couponIssueRepository;

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

    @Nested
    @Sql(
            {
                    "classpath:sql/cleanup.sql",
                    "classpath:sql/kafka/CouponDataForKafka.sql"
            }
    )
    class CouponClaimWithKafka{

        @BeforeEach
        void setup(){
            Coupon coupon1 = Coupon.builder()
                    .couponId(1L)
                    .totalIssued(80L)
                    .remaining(80L)
                    .discountRate(BigDecimal.valueOf(20L))
                    .expireDate(LocalDateTime.now().plusDays(90))
                    .updateDt(LocalDateTime.now())
                    .build();


            Coupon coupon2 = Coupon.builder()
                    .couponId(2L)
                    .totalIssued(80L)
                    .remaining(80L)
                    .discountRate(BigDecimal.valueOf(20L))
                    .expireDate(LocalDateTime.now().plusDays(90))
                    .updateDt(LocalDateTime.now())
                    .build();


            Coupon coupon3 = Coupon.builder()
                    .couponId(3L)
                    .totalIssued(80L)
                    .remaining(80L)
                    .discountRate(BigDecimal.valueOf(20L))
                    .expireDate(LocalDateTime.now().plusDays(90))
                    .updateDt(LocalDateTime.now())
                    .build();


            Coupon coupon4 = Coupon.builder()
                    .couponId(4L)
                    .totalIssued(80L)
                    .remaining(80L)
                    .discountRate(BigDecimal.valueOf(20L))
                    .expireDate(LocalDateTime.now().plusDays(90))
                    .updateDt(LocalDateTime.now())
                    .build();


            couponCacheRepository.cachingCoupon(coupon1);
            couponCacheRepository.cachingCoupon(coupon2);
            couponCacheRepository.cachingCoupon(coupon3);
            couponCacheRepository.cachingCoupon(coupon4);

            System.out.println("쿠폰확인 : " + couponService.getCoupon(1L));

        }
        @Test
        void 쿠폰_4개_80장_100명_동시발급(){
            Coupon cached = couponCacheRepository.getCoupon(1L);
            Long couponStock = couponCacheRepository.getRemainingStock(1L);

            Assertions.assertNotNull(cached);
            assertThat(cached.getCouponId()).isEqualTo(1L);

            assertThat(couponStock).isEqualTo(80L);


            int userCount = 100;
            int couponCount = 4;

            try{
                ExecutorService executor = Executors.newFixedThreadPool(32);
                CountDownLatch latch = new CountDownLatch(userCount * couponCount);

                for (long userId = 1; userId <= userCount; userId++) {
                    for (long couponId = 1; couponId <= couponCount; couponId++) {
                        long finalCouponId = couponId;
                        long finalUserId = userId;

                        executor.submit(() -> {
                            try {
                                var a = couponService.couponClaim(finalCouponId, finalUserId, "CODE-" + finalCouponId + "-" + finalUserId);
                            } catch (Exception e) {
                                // 실패는 그냥 로깅
                                System.out.printf("발급 실패 [couponId=%d, userId=%d] :: %s%n",
                                        finalCouponId, finalUserId, e.getMessage());
                            } finally {
                                latch.countDown();
                            }
                        });
                    }
                }

                latch.await();
                executor.shutdown();

                latch.await();
                executor.shutdown();

                // --- 결과 확인 ---
                for (long couponId = 1; couponId <= couponCount; couponId++) {
                    List<CouponIssue> issedList = couponIssueRepository.findByCouponId(couponId);
                    System.out.printf("쿠폰ID=%d, 발급 개수=%d%n", couponId, issedList.size());
                }
            }catch(Exception e){

            }
        }
    }

}
