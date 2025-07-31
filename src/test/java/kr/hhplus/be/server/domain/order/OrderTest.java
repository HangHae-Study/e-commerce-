package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.OrderLine;
import kr.hhplus.be.server.domain.order.application.repository.OrderLineRepository;
import kr.hhplus.be.server.domain.order.application.repository.OrderRepository;
import kr.hhplus.be.server.domain.order.application.service.OrderService;
import kr.hhplus.be.server.domain.order.testinstance.OrderTestInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class OrderTest {

    @Nested
    @DisplayName("주문 도메인 단위 테스트")
    class OrderDomainUnitTest {

        // --- OrderLine 단위 테스트 ---
        @Test
        void 주문_라인_금액_개수_총합() {
            OrderLine line = OrderTestInstance.simpleOrderLine();

            BigDecimal linePrice = line.getOrderLinePrice();
            int quantity = line.getQuantity();

            assertThat(line.getSubtotal()).isEqualByComparingTo(linePrice.multiply(BigDecimal.valueOf(quantity)));
        }

        @Test
        void 주문_라인_쿠폰_적용_확인() {
            OrderLine line = OrderTestInstance.simpleOrderLine();
            line.applyCoupon(new BigDecimal("20")); // 20% 할인
            // 100 * 0.8 = 80
            BigDecimal linePrice = line.getOrderLinePrice();
            linePrice = linePrice.multiply(BigDecimal.valueOf(0.8));

            assertThat(line.getDiscountPrice()).isEqualByComparingTo(linePrice);
            assertThat(line.getCouponYn()).isEqualTo("Y");
        }

        @Test
        void 주문_라인_주문완료_상태_변경_성공() {
            OrderLine line = OrderTestInstance.simpleOrderLine();
            line.complete();
            assertThat(line.getStatus()).isEqualTo("O_CMPL");
            assertThat(line.getUpdateDt()).isNotNull();
        }

        @Test
        void 주문_라인_주문완료_상태_변경_실패() {
            OrderLine line = OrderTestInstance.simpleOrderLine();
            line.setStatus("O_CMPL");
            assertThatThrownBy(line::complete)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("주문 완료할 수 없는 상태입니다.");
        }

        // --- Order 단위 테스트 ---
        @Test
        void 주문_주문완료_상태_변경_성공() {
            Order order = OrderTestInstance.simpleOrder();
            order.complete();
            assertThat(order.getStatus()).isEqualTo("O_CMPL");
            assertThat(order.getUpdateDt()).isNotNull();
        }

        @Test
        void 주문_주문완료_상태_변경_실패() {
            Order order = OrderTestInstance.simpleOrder();
            order.setStatus("O_CMPL");
            assertThatThrownBy(order::complete)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("주문 완료할 수 없는 상태입니다.");
        }

        @Test
        void 주문_주문내역_미존재_여부_검증_예외() throws Exception {
            Order empty = OrderTestInstance.emptyLinesOrder();
            Method validate = Order.class.getDeclaredMethod("validate");
            validate.setAccessible(true);

            InvocationTargetException ex =
                    assertThrows(InvocationTargetException.class, () -> validate.invoke(empty));
            assertThat(ex.getCause())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("주문 항목이 비어있습니다.");
        }

        @Test
        void 주문_주문내역_총합_여부_검증_예외() throws Exception {
            Order bad = OrderTestInstance.mismatchedTotalOrder();
            Method validate = Order.class.getDeclaredMethod("validate");
            validate.setAccessible(true);

            InvocationTargetException ex =
                    assertThrows(InvocationTargetException.class, () -> validate.invoke(bad));
            assertThat(ex.getCause())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("총합이 일치하지 않습니다.");
        }
    }

    @ExtendWith(MockitoExtension.class)
    @Nested
    @DisplayName("주문 Service 테스트")
    class OrderServicetest{
        @Mock
        private OrderRepository orderRepository;

        @Mock
        private OrderLineRepository orderLineRepository; // 현재 테스트에서는 사용되지 않지만 주입이 필요합니다

        @InjectMocks
        private OrderService orderService;

        private Order sampleOrder;

        @BeforeEach
        void setUp() {
            sampleOrder = OrderTestInstance.persistedOrder();
        }

        @Test
        void createOrder_저장하고_같은_객체를_돌려준다() {
            // given
            when(orderRepository.save(sampleOrder)).thenReturn(sampleOrder);

            // when
            Order result = orderService.createOrder(sampleOrder);

            // then
            assertThat(result).isSameAs(sampleOrder);
            verify(orderRepository).save(sampleOrder);
        }

        @Test
        void getOrder_존재하면_정상조회된다() {
            // given
            when(orderRepository.findById(100L)).thenReturn(Optional.of(sampleOrder));

            // when
            Order result = orderService.getOrder(100L);

            // then
            assertThat(result).isSameAs(sampleOrder);
            verify(orderRepository).findById(100L);
        }

        @Test
        void getOrder_존재하지_않으면_예외를_던진다() {
            // given
            when(orderRepository.findById(999L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.getOrder(999L))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("올바르지 않은 주문 입니다");
            verify(orderRepository).findById(999L);
        }
    }

}
