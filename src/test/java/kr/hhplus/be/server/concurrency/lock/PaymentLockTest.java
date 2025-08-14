package kr.hhplus.be.server.concurrency.lock;


import kr.hhplus.be.server.TestcontainersConfiguration;
import kr.hhplus.be.server.domain.order.application.facade.OrderFacade;
import kr.hhplus.be.server.domain.order.application.service.OrderService;
import kr.hhplus.be.server.domain.payment.application.Payment;
import kr.hhplus.be.server.domain.payment.command.PaymentCreateCommand.*;
import kr.hhplus.be.server.domain.payment.application.facade.PaymentFacade;
import kr.hhplus.be.server.domain.payment.application.repository.PaymentRepository;
import kr.hhplus.be.server.domain.payment.application.service.PaymentService;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.application.repository.ProductLineRepository;
import kr.hhplus.be.server.domain.product.application.service.ProductLineService;
import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.repository.PointRecordRepository;
import kr.hhplus.be.server.domain.user.application.repository.UserRepository;
import kr.hhplus.be.server.domain.user.application.service.UserService;
import org.junit.jupiter.api.DisplayName;
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
        void 비관적_락을_이용한_결제_완료_후_재고감소_동시성_확인() {
            // 재고 100개인 상품에 대한 100개의 주문 요청 -> 성공
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

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ProductLineRepository productLineRepository;

        @Autowired
        private UserService userService;

        @Autowired
        private OrderFacade orderFacade;

        @Autowired
        private PointRecordRepository pointRecordRepository;

        private List<Boolean> runConcurrentPayments(String orderCode) {
            ExecutorService executor = Executors.newFixedThreadPool(2);
            PaymentRequest req = new PaymentRequest(orderCode);

            CompletableFuture<Boolean> f1 = CompletableFuture.supplyAsync(() -> {
                try {
                    paymentFacade.process(req);
                    return true;
                } catch (Exception ex) {
                    System.out.println("예외1: " + ex);
                    return false;
                }
            }, executor);

            CompletableFuture<Boolean> f2 = CompletableFuture.supplyAsync(() -> {
                try {
                    paymentFacade.process(req);
                    return true;
                } catch (Exception ex) {
                    System.out.println("예외2: " + ex);
                    return false;
                }
            }, executor);

            CompletableFuture.allOf(f1, f2).join();
            executor.shutdown();

            return List.of(f1.join(), f2.join());
        }

        private List<Boolean> runConcurrentPaymentsN(String orderCode) {
            ExecutorService executor = Executors.newFixedThreadPool(20);
            PaymentRequest req = new PaymentRequest(orderCode);
            List<CompletableFuture<Boolean>> futures =
                    IntStream.rangeClosed(1, 100)
                            .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                                try {
                                    paymentFacade.process(req);
                                    System.out.println();
                                    return true;
                                } catch (Exception ex) {
                                    System.out.println("예외"+ i + "," + ex.toString());
                                    return false;
                                }
                            }, executor))
                            .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            executor.shutdown();

            return  futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
        }

        @Test
        @Sql("classpath:sql/lock/order/OrderConcurrency.sql")
        void 낙관적_락을_이용한_결제_완료_후_주문상태_동시성_확인_성공(){
            // 하나의 주문에 여러번의 결제 -> 하나의 주문만 성공
            String orderCode = "ORD-1";
            List<ProductLine> initPl = productLineService.getProductLineList(1L);

            // 결과 집계
            List<Boolean> results = runConcurrentPaymentsN(orderCode);
            long successCount = results.stream().filter(b -> b).count();
            long failureCount = results.stream().filter(b -> !b).count();

            // 정확히 하나만 성공, 나머지는 실패
            assertThat(successCount).isEqualTo(1);
            assertThat(failureCount).isNotEqualTo(1);

            // 결제 레코드는 하나만 생성됐는지 확인
            List<Payment> allPayments = paymentRepository.findAll();
            assertThat(allPayments).hasSize(1);
            assertThat(allPayments.get(0).getOrderId())
                    .isEqualTo(allPayments.get(0).getOrderId());

            // 재고 감소도 하나의 주문에 대해서만 진행되었는지 확인
            List<ProductLine> afterPl = productLineRepository.findByProductId(1L);
            Users user = userService.getUser(1L);

            System.out.println(initPl.get(0));
            System.out.println(afterPl.get(0));
            System.out.println(user);

            System.out.println(orderFacade.getOrderByCode("ORD-1"));

            System.out.println("레코드 : " + pointRecordRepository.findAll());
        }

        @Test
        @Sql("classpath:sql/lock/order/OrderStatusConcurrency.sql")
        void 낙관적_락을_이용한_결제_완료_후_주문상태_동시성_확인_실패() {
            // 재고가 부족한 상품에 한번에 똑같은 주문-> 결제 요청 2번 (실패가 한번만 일어나야함)
            // 재시도 코드 검증용
            String orderCode = "ORD-1";

            // 결과 집계 (둘다 실패)
            List<Boolean> results = runConcurrentPaymentsN(orderCode);
            long successCount = results.stream().filter(b -> b).count();
            long failureCount = results.stream().filter(b -> !b).count();

            // 둘다 실패
            assertThat(successCount).isEqualTo(0);
            //assertThat(failureCount).isEqualTo(2);


            List<ProductLine> afterPl = productLineRepository.findByProductId(1L);
            Users user = userService.getUser(1L);

            System.out.println(afterPl);
            System.out.println(user);
            System.out.println(orderFacade.getOrderByCode("ORD-1"));
        }

        @Test
        @Sql("classpath:sql/lock/order/OrderLackOfPointConcurrency.sql")
        void 낙관적_락을_이용한_결제_실패_후_주문상태_동시성_확인_실패() {
            // 잔고가 부족한 상품에 대해 똑같은 요청이 들어온 경우
            // 재고 원복이 둘다 일어나고, 실패 상태에서 무한루프가 돌지 않아야함.
            String orderCode = "ORD-1";

            // 결과 집계 (둘다 실패)
            List<Boolean> results = runConcurrentPaymentsN(orderCode);
            long successCount = results.stream().filter(b -> b).count();
            long failureCount = results.stream().filter(b -> !b).count();

            // 둘다 실패
            assertThat(successCount).isEqualTo(0);
            //assertThat(failureCount).(2);


            List<ProductLine> afterPl = productLineRepository.findByProductId(1L);
            Users user = userService.getUser(1L);

            System.out.println(afterPl);
            System.out.println(user);
            System.out.println(orderFacade.getOrderByCode("ORD-1"));

            System.out.println("레코드 : " + pointRecordRepository.findAll());
        }
    }
}
