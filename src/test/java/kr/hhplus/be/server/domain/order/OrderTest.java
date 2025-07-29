package kr.hhplus.be.server.domain.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import kr.hhplus.be.server.domain.coupon.application.repository.CouponIssueRepository;
import kr.hhplus.be.server.domain.order.adapter.repository.OrderJpaRepository;
import kr.hhplus.be.server.domain.order.adapter.repository.OrderJpaRepositoryAdapter;
import kr.hhplus.be.server.domain.order.adapter.repository.OrderLineJpaRepository;
import kr.hhplus.be.server.domain.order.adapter.repository.OrderLineJpaRepositoryAdapter;
import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.OrderLine;
import kr.hhplus.be.server.domain.order.application.dto.OrderCreateRequest;
import kr.hhplus.be.server.domain.order.application.repository.OrderLineRepository;
import kr.hhplus.be.server.domain.order.application.repository.OrderRepository;
import kr.hhplus.be.server.domain.order.application.service.OrderService;
import kr.hhplus.be.server.domain.product.application.repository.ProductLineRepository;
import kr.hhplus.be.server.domain.product.application.repository.ProductRepository;
import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderTest {
/*

    @Nested @DisplayName("주문 도메인 테스트")
    @ExtendWith(MockitoExtension.class)
    class OrderDomainTest{

        @Test
        void 주문_정보_금액_합계_비교(){
            TestInstance.MockOrder mockOrder = getMockOrder();
            Order o = mockOrder.orderData;
            List<OrderLine> ol = mockOrder.oLineData;

            BigDecimal sum = ol.stream()
                    .map(OrderLine::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            assertThat(o.getTotalPrice()).isEqualByComparingTo(sum);
        }

        @Test
        void 주문_쿠폰_적용_금액_비교(){
            TestInstance.MockOrder mockOrder = getMockOrder();
            Order o = mockOrder.orderData;
            List<OrderLine> ol = mockOrder.oLineData;

            CouponIssue coupon = CouponIssue.builder()
                    .couponCode("TWENTY_PERCENT_COUPON")
                    .discountRate(BigDecimal.valueOf(20))
                    .build();

            ol.forEach(line -> {
                line.applyCoupon(coupon);

                BigDecimal expected = line.getSubtotal()
                        .multiply(BigDecimal.valueOf(100).subtract(coupon.getDiscountRate()))
                        .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);

                assertThat(line.getDiscountPrice())
                        .isEqualByComparingTo(expected);
            });
        }
    }

    @SpringBootTest
    //@Transactional
    @Nested @DisplayName("주문 서비스 Jpa Persistence 테스트")
    class OrderServiceTest{
        private OrderService orderService;

        @Autowired
        private OrderJpaRepository orderJpaRepository;

        @Autowired
        private OrderLineJpaRepository orderLineJpaRepository;

        private OrderRepository orderRepository;
        private OrderLineRepository orderLineRepository;

        @BeforeEach
        void setup(){
            orderRepository = new OrderJpaRepositoryAdapter(orderJpaRepository);
            orderLineRepository = new OrderLineJpaRepositoryAdapter(orderLineJpaRepository);
            orderService = new OrderService(orderRepository, orderLineRepository);
        }


        @Test
        void 주문_정보_생성(){
            TestInstance.PersistOrder order = getPersistOrder();

            Order newOrder = order.orderData;

            orderService.createOrder(order.orderData);
            Order selectOrder = orderService.getOrder(order.orderData.getOrderId());

            OrderLine newOrderLine = newOrder.getOrderLines().get(0);
            OrderLine selectOrderLine = selectOrder.getOrderLines().get(0);

            assertThat(newOrderLine.getOrderId()).isEqualTo(selectOrder.getOrderId());

            assertThat(selectOrderLine.getOrderLineId()).isNotEqualTo(null);
            //assertThat(selectOrderLine.getOrderLineId()).isEqualTo(1L);
            assertThat(selectOrderLine.getOrderId()).isEqualTo(newOrderLine.getOrderId());
            assertThat(selectOrder.getOrderLines()).hasSizeGreaterThan(1);
        }

        @Test
        void 주문_정보_생성_쿠폰_적용(){
            TestInstance.PersistOrder order = getPersistOrder();

            Order newOrder = order.orderData;
            CouponIssue coupon = CouponIssue.builder()
                    .couponCode("TWENTY_PERCENT_COUPON")
                    .discountRate(BigDecimal.valueOf(20))
                    .build();

            orderService.createOrder(order.orderData, coupon);
            Order selectOrder = orderService.getOrder(order.orderData.getOrderId());

            OrderLine newOrderLine = newOrder.getOrderLines().get(0);
            OrderLine selectOrderLine = selectOrder.getOrderLines().get(0);

            assertThat(newOrderLine.getOrderId()).isEqualTo(selectOrder.getOrderId());

            assertThat(selectOrderLine.getOrderLineId()).isNotEqualTo(null);

            assertThat(selectOrder.getOrderLines()).hasSize(newOrder.getOrderLines().size());
            assertThat(selectOrderLine.getOrderId()).isEqualTo(newOrderLine.getOrderId());

            assertThat(selectOrderLine.getCouponYn()).isEqualTo("Y");
            assertThat(selectOrderLine.getCouponCode()).isEqualTo(coupon.getCouponCode());
            assertThat(selectOrderLine.getDiscountPrice()).isNotEqualTo(null);
        }
    }

    @Nested @DisplayName("주문 컨트롤러 테스트")
    @SpringBootTest
    @AutoConfigureMockMvc
    class OrderControllerTest {

        @Autowired
        private org.springframework.test.web.servlet.MockMvc mockMvc;

        @Autowired
        private UserRepository userRepo;

        @Autowired
        private ProductRepository productRepo;

        @Autowired
        private ProductLineRepository productLineRepo;

        @Autowired
        private CouponIssueRepository couponIssueRepo;

        private Long productLineId1;
        private Long productLineId2;
        private BigDecimal productLinePrice1;
        private BigDecimal productLinePrice2;
        private final String couponCode = "CPN1";

        @BeforeEach
        void setUp() {
            // 1) 유저 저장 (id = 1L)
            //userRepo.save(new Users(1L));

            // 2) 상품 및 상품 라인 저장
            var pp = TestInstance.PersistProduct.getPersistProduct();
            var product = productRepo.save(pp.product);
            var line1 = productLineRepo.save(pp.productLine1);
            var line2 = productLineRepo.save(pp.productLine2);
            productLineId1 = line1.getProductLineId();
            productLineId2 = line2.getProductLineId();
            productLinePrice1 = line1.getProductLinePrice();
            productLinePrice2 = line2.getProductLinePrice();

            // 3) 쿠폰 발급 저장
            CouponIssue ci = CouponIssue.builder()
                    .couponIssueId(null)
                    .couponCode(couponCode)
                    .couponId(1L)
                    .userId(1L)
                    .couponValid("Y")
                    .discountRate(BigDecimal.valueOf(20))  // 20%
                    .expireDate(LocalDateTime.now().plusDays(1))
                    .updateDt(LocalDateTime.now())
                    .build();
            couponIssueRepo.save(ci);
        }

        @Test
        void createOrder_withCoupon_success() throws Exception {
            var items = List.of(
                    new OrderCreateRequest.OrderItem(productLineId1, productLinePrice1, 1),
                    new OrderCreateRequest.OrderItem(productLineId2, productLinePrice2, 1)
            );
            var req = new OrderCreateRequest(
                    "ORDER-001",
                    1L,
                    BigDecimal.ZERO,  // totalPrice는 서비스에서 재계산됨
                    items,
                    couponCode
            );

            BigDecimal expected1 = productLinePrice1
                    .multiply(BigDecimal.valueOf(80))
                    .divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
            BigDecimal expected2 = productLinePrice2
                    .multiply(BigDecimal.valueOf(80))
                    .divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
            BigDecimal expectedTotal = expected1.add(expected2);

            mockMvc.perform(post("/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(req)))
                    .andExpect(status().isOk()) // isCreated()
                    .andExpect(jsonPath("$.data.orderId").value("ORDER-001"))
                    .andExpect(jsonPath("$.data.items", hasSize(2)))
                    .andExpect(jsonPath("$.data.items[0].productLineId").value(productLineId1))
                    .andExpect(jsonPath("$.data.items[0].couponYN").value("Y"))
                    .andExpect(jsonPath("$.data.items[0].discountPrice").value(expected1.intValue()))
                    .andExpect(jsonPath("$.data.items[1].productLineId").value(productLineId2))
                    .andExpect(jsonPath("$.data.items[1].couponYN").value("Y"))
                    .andExpect(jsonPath("$.data.items[1].discountPrice").value(expected2.intValue()))
                    .andExpect(jsonPath("$.data.totalPrice").value(expectedTotal.intValue()))
                    .andExpect(jsonPath("$.data.orderStatus").value("O_MAKE"));
        }

        private static String asJsonString(Object obj) {
            try {
                return new ObjectMapper().writeValueAsString(obj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

*/

}
