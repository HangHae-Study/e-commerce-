package kr.hhplus.be.server.domain.payment;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;

import kr.hhplus.be.server.domain.order.application.service.OrderService;
import kr.hhplus.be.server.domain.payment.application.Payment;
import kr.hhplus.be.server.domain.payment.application.dto.PaymentRequest;
import kr.hhplus.be.server.domain.payment.application.dto.PaymentResponse;
import kr.hhplus.be.server.domain.payment.application.repository.PaymentFacade;
import kr.hhplus.be.server.domain.payment.application.repository.PaymentRepository;
import kr.hhplus.be.server.domain.payment.application.service.PaymentService;
import kr.hhplus.be.server.domain.product.application.facade.InventoryFacade;
import kr.hhplus.be.server.domain.user.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.user.application.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

public class PaymentTest{

    @Nested
    @DisplayName("결제 Service & Repository 테스트")
    @SpringBootTest
    @Transactional
    class PaymentServiceIntegrationTest {

        @Autowired
        private PaymentService paymentService;

        @Autowired
        private PaymentRepository paymentRepository;

        @Test
        void 결제요청_성공_Repo_저장확인() {
            // --- when ---
            Long userId     = 42L;
            Long orderId    = 77L;
            BigDecimal amount = new BigDecimal("123.45");

            Payment result = paymentService.pay(userId, orderId, amount);

            // --- then ---
            // 1) 저장된 ID 가 생성되었는지
            assertThat(result.getPaymentId()).isNotNull();

            // 2) 반환된 객체의 필드들이 요청값과 일치하는지
            assertThat(result.getUserId()).isEqualTo(userId);
            assertThat(result.getOrderId()).isEqualTo(orderId);
            assertThat(result.getTotalPrice()).isEqualByComparingTo(amount);
            assertThat(result.getStatus()).isEqualTo("P_CMPL");

            // 3) 실제 DB 에도 동일한 값이 저장되었는지 조회해 검증
            Optional<Payment> fromDbOpt = paymentRepository.findById(result.getPaymentId());
            assertThat(fromDbOpt).isPresent();

            Payment fromDb = fromDbOpt.get();
            assertThat(fromDb.getPaymentId()).isEqualTo(result.getPaymentId());
            assertThat(fromDb.getUserId()).isEqualTo(userId);
            assertThat(fromDb.getOrderId()).isEqualTo(orderId);
            assertThat(fromDb.getTotalPrice()).isEqualByComparingTo(amount);
            assertThat(fromDb.getStatus()).isEqualTo("P_CMPL");
        }
    }


    @Nested
    @ExtendWith(MockitoExtension.class)
    @DisplayName("결제 퍼사드(서비스) 테스트")
    class PaymentFacadeTest{
        @Mock
        OrderService orderService;
        @Mock
        InventoryFacade inventoryFacade;
        @Mock
        UserService userService;
        @Mock
        PaymentService paymentService;

        @InjectMocks
        private PaymentFacade paymentFacade;

        private PaymentRequest req;
        private Order order;

        @BeforeEach
        void setUp() {
            // 공통 리퀘스트, 도메인 객체 준비
            req = new PaymentRequest("ORD-ABC-123");
            order = Order.builder()
                    .orderId(42L)
                    .orderCode(req.orderCode())
                    .userId(7L)
                    .totalPrice(new BigDecimal("1500"))
                    .orderLines(Collections.emptyList())
                    .status("O_MAKE")
                    .build();
        }

        @Test
        void 정상_결제_처리_주문상태변경_결제정보반환() {
            // given
            Payment paid = Payment.builder()
                    .paymentId(99L)
                    .orderId(order.getOrderId())
                    .userId(order.getUserId())
                    .paymentDt(LocalDateTime.now())
                    .totalPrice(order.getTotalPrice())
                    .status("P_CMPL")
                    .build();

            given(orderService.getOrderByCode(req.orderCode())).willReturn(order);
            willDoNothing().given(inventoryFacade).checkStock(order);
            given(userService.usePoint(order.getUserId(), order.getTotalPrice()))
                    .willReturn(Users.builder().userId(order.getUserId())
                            .balance(new BigDecimal("500"))
                            .build());
            // orderComplete
            willDoNothing().given(orderService).orderComplete(order);
            given(paymentService.pay(order.getUserId(), order.getOrderId(), order.getTotalPrice()))
                    .willReturn(paid);

            // when
            PaymentResponse resp = paymentFacade.process(req);

            // then
            assertThat(resp.paymentId()).isEqualTo(99L);
            assertThat(resp.orderId()).isEqualTo(42L);
            assertThat(resp.totalPrice()).isEqualByComparingTo(new BigDecimal("1500"));
            assertThat(resp.paymentStatus()).isEqualTo("P_CMPL");
            // orderComplete 호출
            verify(orderService, times(1)).orderComplete(order);
            // 복구 로직은 호출되지 않아야 함
            verify(inventoryFacade, never()).restoreStock(any());
        }

        // 결제시패(주문실패) 상태
        @Test
        void 재고_부족_예외_검증() {
            given(orderService.getOrderByCode(req.orderCode())).willReturn(order);
            willThrow(new IllegalStateException("재고가 부족합니다."))
                    .given(inventoryFacade).checkStock(order);

            assertThatThrownBy(() -> paymentFacade.process(req))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("재고가 부족합니다.");

            verify(inventoryFacade).restoreStock(order);
            verify(orderService, never()).orderComplete(any());
            verify(userService, never()).usePoint(anyLong(), any());
            verify(paymentService, never()).pay(anyLong(), anyLong(), any());
        }

        @Test
        void 포인트_부족_예외_검증() {
            given(orderService.getOrderByCode(req.orderCode())).willReturn(order);
            willDoNothing().given(inventoryFacade).checkStock(order);
            willThrow(new IllegalStateException("잔액이 부족합니다."))
                    .given(userService).usePoint(order.getUserId(), order.getTotalPrice());

            assertThatThrownBy(() -> paymentFacade.process(req))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("잔액이 부족합니다.");

            verify(inventoryFacade).restoreStock(order);
            verify(orderService, never()).orderComplete(any());
            verify(paymentService, never()).pay(anyLong(), anyLong(), any());
        }

        @Test
        void 결제_실패_복구_호출() {
            given(orderService.getOrderByCode(req.orderCode())).willReturn(order);
            willDoNothing().given(inventoryFacade).checkStock(order);
            given(userService.usePoint(order.getUserId(), order.getTotalPrice()))
                    .willReturn(Users.builder().userId(order.getUserId())
                            .balance(new BigDecimal("500"))
                            .build());
            willDoNothing().given(orderService).orderComplete(order);
            willThrow(new RuntimeException("결제 시스템 장애"))
                    .given(paymentService).pay(order.getUserId(), order.getOrderId(), order.getTotalPrice());

            assertThatThrownBy(() -> paymentFacade.process(req))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("결제 시스템 장애");

            // stock 복구
            verify(inventoryFacade).restoreStock(order);
            // orderComplete 은 결제 직전에 이미 호출됨
            verify(orderService).orderComplete(order);
        }
    }
}