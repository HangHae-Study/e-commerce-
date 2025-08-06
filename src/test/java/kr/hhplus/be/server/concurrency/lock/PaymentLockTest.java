package kr.hhplus.be.server.concurrency.lock;


import kr.hhplus.be.server.TestcontainersConfiguration;
import kr.hhplus.be.server.domain.order.application.service.OrderService;
import kr.hhplus.be.server.domain.payment.application.dto.PaymentRequest;
import kr.hhplus.be.server.domain.payment.application.facade.PaymentFacade;
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
    @Sql("classpath:sql/lock/Stock_Concurrency.sql")
    class PaymentLock {

        @Autowired
        private PaymentFacade paymentFacade;

        @Autowired
        private ProductLineService productLineService;

        @Test
        void 락을_이용한_결제_프로세스_실행() {
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
    }
}
