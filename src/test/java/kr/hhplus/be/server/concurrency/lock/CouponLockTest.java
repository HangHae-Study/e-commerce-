package kr.hhplus.be.server.concurrency.lock;

import kr.hhplus.be.server.TestcontainersConfiguration;
import kr.hhplus.be.server.domain.coupon.application.Coupon;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import kr.hhplus.be.server.domain.coupon.application.repository.CouponIssueRepository;
import kr.hhplus.be.server.domain.coupon.application.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.application.service.CouponService;
import kr.hhplus.be.server.domain.user.application.service.UserService;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        TestcontainersConfiguration.class,
        UserService.class
})
public class CouponLockTest {
    @SpringBootTest
    @Nested
    class CouponLock {

        @Autowired
        private CouponIssueRepository couponIssueRepository;

        @Autowired
        private CouponRepository couponRepository;

        @Autowired
        private CouponService couponService;

        @Test
        @Sql("classpath:sql/lock/coupon/CouponRestoreConcurrency.sql")
        void 낙관적_락을_이용한_쿠폰원복_확인() {
            Long couponIssueId = 1L;
            String couponIssueCode = "CPN-1";

            ExecutorService executor = Executors.newFixedThreadPool(2);

            CompletableFuture<Boolean> f1 = CompletableFuture.supplyAsync(() -> {
                try {
                    couponService.couponRestoreByPayment(1L,couponIssueCode);
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }, executor);

            CompletableFuture<Boolean> f2 = CompletableFuture.supplyAsync(() -> {
                try {
                    couponService.couponRestoreByPayment(1L,couponIssueCode);
                    return true;
                } catch (Exception ex) {
                    System.out.println(ex);
                    return false;
                }
            }, executor);

            CompletableFuture.allOf(f1, f2).join();
            executor.shutdown();

            List<Boolean> results = List.of(f1.join(), f2.join());
            long successCount = results.stream().filter(b -> b).count();
            long failureCount = results.stream().filter(b -> !b).count();

            // 하나만 성공, 하나는 낙관적 락 충돌로 실패
            assertThat(successCount).isEqualTo(1);
            assertThat(failureCount).isEqualTo(1);

            // 최종 쿠폰 상태 확인: couponValid = 'Y', version = 2
            CouponIssue issue = couponIssueRepository.findById(couponIssueId)
                    .orElseThrow();
            assertThat(issue.getCouponValid()).isEqualTo("Y");
            assertThat(issue.getVersion()).isEqualTo(2L);
        }

        @Test
        @Sql("classpath:sql/lock/coupon/CouponIssueLock.sql")
        void 쿠폰_동시_발급_테스트(){
            Long couponId = 1L;

            ExecutorService executor = Executors.newFixedThreadPool(20);
            List<CompletableFuture<Boolean>> futures = LongStream.rangeClosed(1, 100)
                    .mapToObj(userId -> CompletableFuture.supplyAsync(() -> {
                        try {
                            couponService.newCouponIssue(userId, couponId);
                            return true;
                        } catch (Exception ex) {
                            return false;
                        }
                    }, executor))
                    .toList();

            // 모두 완료 대기
            CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[0])
            ).join();
            executor.shutdown();

            // 성공/실패 집계
            long successCount = futures.stream()
                    .map(CompletableFuture::join)
                    .filter(b -> b)
                    .count();
            long failureCount = futures.size() - successCount;

            Coupon coupon = couponRepository.findById(couponId).orElseThrow();


            System.out.println("성공 횟수 : " + successCount);
            System.out.println("실패 횟수 : " + failureCount);
            System.out.println("쿠폰 잔량 : " + coupon.getRemaining());

            // 모두 성공해야 하므로 failureCount == 0
            assertThat(successCount).isEqualTo(100);
            assertThat(failureCount).isZero();

            // 쿠폰 remaining은 0
            assertThat(coupon.getRemaining()).isZero();

            // coupon_issue 테이블에 100건 발급
            List<CouponIssue> issues = couponIssueRepository.findAll();
            assertThat(issues).hasSize(100);
        }

    }
}
