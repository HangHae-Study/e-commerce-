package kr.hhplus.be.server.concurrency.transaction;


import kr.hhplus.be.server.TestcontainersConfiguration;
import kr.hhplus.be.server.domain.order.application.service.OrderService;
import kr.hhplus.be.server.domain.payment.application.dto.PaymentRequest;
import kr.hhplus.be.server.domain.payment.application.facade.PaymentFacade;
import kr.hhplus.be.server.domain.payment.application.service.PaymentService;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.application.repository.ProductLineRepository;
import kr.hhplus.be.server.domain.product.exception.OutOfStockException;
import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.dto.PointDao;
import kr.hhplus.be.server.domain.user.application.repository.PointRepository;
import kr.hhplus.be.server.domain.user.application.service.UserService;
import kr.hhplus.be.server.domain.user.exception.InsufficientBalanceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        TestcontainersConfiguration.class,
        PaymentService.class,
        OrderService.class,
        UserService.class
})
public class PaymentTransactionTest {


    @SpringBootTest
    @Nested
    @DisplayName("재고 관련 트랜잭션 확인 테스트")
    @Sql("classpath:sql/transaction/inventory/OutOfStock.sql")
    class InventoryTransaction{
        @Autowired private PaymentFacade paymentFacade;
        @Autowired private PointRepository pointRepository;
        @Autowired private ProductLineRepository productLineRepository;

        @Test
        // Transaction이 PaymentFacade 단위 -> 하위 계층 서비스 단위로 변경
        void 결제_중_예외발생시_재고량_유지_트랜잭션_검증(){
            // 유저 1 결제 실패
            PaymentRequest req = new PaymentRequest("ORD-1-FAIL");
            Optional<PointDao> beforePoint = pointRepository.findByUserId(1L);
            List<ProductLine> beforeLines = productLineRepository.findByProductId(1L);

            assertThatThrownBy(() -> paymentFacade.process(req))
                .isInstanceOf(IllegalStateException.class);

            // 포인트 및 재고 변화 없음
            Optional<PointDao> afterPoint = pointRepository.findByUserId(1L);
            assertThat(beforePoint.get().getBalance()).isEqualByComparingTo(afterPoint.get().getBalance());

            // 재고 변화 없음
            List<ProductLine> afterLines = productLineRepository.findByProductId(1L);
            for (int i = 0; i<beforeLines.size(); i++){
                assertThat(beforeLines.get(i).getRemaining()).isEqualTo(
                        afterLines.get(i).getRemaining()
                );
            }

            // 유저 2 결제 성공
            PaymentRequest req2 = new PaymentRequest("ORD-2-SUCC");
            Optional<PointDao> beforePoint2 = pointRepository.findByUserId(2L);

            paymentFacade.process(req2);

            // 포인트 및 재고 변화 있음
            Optional<PointDao> afterPoint2 = pointRepository.findByUserId(2L);
            assertThat(beforePoint2.get().getBalance()).isNotEqualByComparingTo(afterPoint2.get().getBalance());

            // 재고 변화 있음
            List<ProductLine> afterLines2 = productLineRepository.findByProductId(1L);
            for (int i = 0; i<beforeLines.size(); i++){
                assertThat(beforeLines.get(i).getRemaining()).isNotEqualTo(
                        afterLines2.get(i).getRemaining()
                );
            }
        }

    }

    @SpringBootTest
    @Nested
    @DisplayName("잔고 관련 트랜잭션 확인 테스트")
    @Sql("classpath:sql/transaction/point/InsufficientBalance.sql")
    class PointTransaction{
        @Autowired private PaymentFacade paymentFacade;
        @Autowired private PointRepository pointRepository;
        @Autowired private ProductLineRepository productLineRepository;

        @Test
        void 결제_중_예외발생시_포인트_유지_트랜잭션_검증(){
            // 유저 1 결제 실패
            PaymentRequest req = new PaymentRequest("ORD-1-FAIL");
            Optional<PointDao> beforePoint = pointRepository.findByUserId(1L);
            List<ProductLine> beforeLines = productLineRepository.findByProductId(1L);

            assertThatThrownBy(() -> paymentFacade.process(req))
                    .isInstanceOf(IllegalStateException.class);

            // 포인트 및 재고 변화 없음
            Optional<PointDao> afterPoint = pointRepository.findByUserId(1L);
            assertThat(beforePoint.get().getBalance()).isEqualByComparingTo(afterPoint.get().getBalance());

            // 재고 변화 없음
            List<ProductLine> afterLines = productLineRepository.findByProductId(1L);
            for (int i = 0; i<beforeLines.size(); i++){
                assertThat(beforeLines.get(i).getRemaining()).isEqualTo(
                        afterLines.get(i).getRemaining()
                );
            }
        }
    }


}
