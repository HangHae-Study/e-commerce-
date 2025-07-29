package kr.hhplus.be.server.layered;

import kr.hhplus.be.server.TestData.TestInstance;
import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.repository.OrderRepository;
import kr.hhplus.be.server.domain.order.application.service.OrderService;
import kr.hhplus.be.server.domain.payment.application.dto.PaymentRequest;
import kr.hhplus.be.server.domain.payment.application.dto.PaymentResponse;
import kr.hhplus.be.server.domain.payment.application.repository.PaymentFacade;
import kr.hhplus.be.server.domain.payment.application.repository.PaymentRepository;
import kr.hhplus.be.server.domain.product.application.repository.ProductLineRepository;
import kr.hhplus.be.server.domain.product.application.service.ProductLineService;
import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.repository.PointRepository;
import kr.hhplus.be.server.domain.user.application.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
class PaymentFacadeIntegrationTest {

    @Autowired
    PaymentFacade paymentFacade;

    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProductLineService productLineService;
    @Autowired
    ProductLineRepository productLineRepository;

    @Autowired
    UserRepository userRepository;

    //@Autowired
    //UserPointFacade pointFacade;
    @Autowired
    PointRepository pointRepository;

    @Autowired
    PaymentRepository paymentRepository;

    private Long productLineId1;
    private Long productLineId2;
    private BigDecimal productLinePrice1;
    private BigDecimal productLinePrice2;

    @BeforeEach
    void 세팅() {
        // 기본 데이터 삽입 - 필요시 초기화
    }

    @Test
    void 결제_정상_처리된다() {
        // given
        Long userId = 1L;
        String orderId = "ORDER_MOCK_ID";

        var pp = TestInstance.PersistProduct.getPersistProduct();
        var line1 = productLineRepository.save(pp.productLine1);
        var line2 = productLineRepository.save(pp.productLine2);
        productLineId1 = line1.getProductLineId();
        productLineId2 = line2.getProductLineId();
        productLinePrice1 = line1.getProductLinePrice();
        productLinePrice2 = line2.getProductLinePrice();

        TestInstance.PersistPayment payInfo = new TestInstance.PersistPayment();
        Order order = payInfo.orderData;

        orderRepository.save(order);

        // 포인트 충전
        //userRepository.save(new Users(1L));
       // pointRepository.save(new Point(userId, BigDecimal.valueOf(70000.0)));

        // 재고 등록
        productLineRepository.save(line1);
        productLineRepository.save(line2);

        // when
        PaymentResponse response = paymentFacade.process(new PaymentRequest(orderId));

        // then
        assertThat(response.paymentStatus()).isEqualTo("P_CMPL");

    }

    /*
    @Test
    void 잔고_부족_시_예외() {
        // given
        Long userId = 2L;
        String orderId = "ORD-NO-POINT";
        BigDecimal price = BigDecimal.valueOf(5000);

        Order order = TestOrderFactory.create(orderId, userId, price);
        orderRepository.save(order);

        pointRepository.save(new Point(userId, BigDecimal.valueOf(1000)));

        for (OrderLine line : order.getOrderLines()) {
            productLineRepository.save(new ProductLine(line.getProductLineId(), 10L));
        }

        // when & then
        assertThatThrownBy(() -> paymentFacade.process(new PaymentRequest(userId.toString(), orderId)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("잔고 부족");
    }

    @Test
    void 재고_부족_시_예외() {
        // given
        Long userId = 3L;
        String orderId = "ORD-NO-STOCK";
        BigDecimal price = BigDecimal.valueOf(1000);

        Order order = TestOrderFactory.create(orderId, userId, price);
        orderRepository.save(order);

        pointRepository.save(new Point(userId, BigDecimal.valueOf(2000)));

        for (OrderLine line : order.getOrderLines()) {
            productLineRepository.save(new ProductLine(line.getProductLineId(), 0L)); // 재고 0
        }

        // when & then
        assertThatThrownBy(() -> paymentFacade.process(new PaymentRequest(userId.toString(), orderId)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("재고 소진");
    }
     */
}