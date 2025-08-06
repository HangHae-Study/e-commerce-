package kr.hhplus.be.server.concurrency.lock;


import kr.hhplus.be.server.TestcontainersConfiguration;
import kr.hhplus.be.server.domain.order.application.service.OrderService;
import kr.hhplus.be.server.domain.payment.application.Payment;
import kr.hhplus.be.server.domain.payment.application.dto.PaymentRequest;
import kr.hhplus.be.server.domain.payment.application.facade.PaymentFacade;
import kr.hhplus.be.server.domain.payment.application.repository.PaymentRepository;
import kr.hhplus.be.server.domain.payment.application.service.PaymentService;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.application.service.ProductLineService;
import kr.hhplus.be.server.domain.user.application.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.N;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        TestcontainersConfiguration.class,
        PaymentService.class,
        OrderService.class,
        UserService.class
})
public class PaymentLockTest {
    @SpringBootTest
    @Nested
    @DisplayName("결제 중 락 확인 테스트")
    class PaymentLock {

        @Autowired
        private PaymentFacade paymentFacade;

        @Autowired
        private ProductLineService productLineService;

        @Autowired
        private PaymentRepository paymentRepository;

        @Test
        @Sql("classpath:sql/lock/StockConcurrency.sql")
        void 비관적_락을_이용한_결제_프로세스_실행_재고확인() {
            int userCount = 100;
            ExecutorService executor = Executors.newFixedThreadPool(20);

            // 1번 상품라인에 대한 100개의 비동기 결제 요청
            List<CompletableFuture<Boolean>> futures =
                    IntStream.rangeClosed(1, userCount)
                            .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                                try {
                                    paymentFacade.process(new PaymentRequest("ORD-" + i));
                                    return true;
                                } catch (Exception ex) {
                                    System.out.println(ex.toString());
                                    return false;
                                }
                            }, executor))
                            .toList();

            // 모두 완료 대기
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            executor.shutdown();

            // 성공한 요청 수
            long successCount = futures.stream()
                    .map(CompletableFuture::join)
                    .filter(b -> b)
                    .count();

            // 남은 재고 총합 (product_id = 1)
            List<ProductLine> lines = productLineService.getProductLineList(1L);
            long remainingTotal = lines.stream()
                    .mapToLong(ProductLine::getRemaining)
                    .sum();

            System.out.println("성공한 결제 요청 수: " + successCount);
            System.out.println("남은 재고 총합: " + remainingTotal);

            // 락 없이 동시성 문제가 있으면
            // successCount < 100, remainingTotal > (100 - successCount) 이 됩니다.
            assertThat(successCount).isEqualTo(100);
            assertThat(remainingTotal).isEqualTo(0);
        }

        @Test
        @Sql("classpath:sql/lock/OrderConcurrency.sql")
        void 낙관적_락을_이용한_결제_프로세스_실행_주문상태확인(){
            String orderCode = "ORD-1";
            PaymentRequest req = new PaymentRequest(orderCode);

            ExecutorService executor = Executors.newFixedThreadPool(2);

            // 둘 다 거의 동시에 실행하여, 하나만 성공하길 기대
            CompletableFuture<Boolean> f1 = CompletableFuture.supplyAsync(() -> {
                try {
                    paymentFacade.process(req);
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }, executor);

            CompletableFuture<Boolean> f2 = CompletableFuture.supplyAsync(() -> {
                try {
                    paymentFacade.process(req);
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }, executor);

            // 두 작업이 끝날 때까지 대기
            CompletableFuture.allOf(f1, f2).join();
            executor.shutdown();

            // 결과 집계
            List<Boolean> results = List.of(f1.join(), f2.join());
            long successCount = results.stream().filter(b -> b).count();
            long failureCount = results.stream().filter(b -> !b).count();

            // 정확히 하나만 성공, 하나는 실패
            assertThat(successCount).isEqualTo(1);
            assertThat(failureCount).isEqualTo(1);

            // 결제 레코드는 하나만 생성됐는지 확인
            List<Payment> allPayments = paymentRepository.findAll();
            assertThat(allPayments).hasSize(1);
            assertThat(allPayments.get(0).getOrderId())
                    .isEqualTo(allPayments.get(0).getOrderId());
        }
    }


}
