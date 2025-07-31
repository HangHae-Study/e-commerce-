package kr.hhplus.be.server.domain.order.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import kr.hhplus.be.server.domain.coupon.application.service.CouponService;
import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.OrderLine;
import kr.hhplus.be.server.domain.order.application.facade.OrderFacade;
import kr.hhplus.be.server.domain.order.application.service.OrderService;
import kr.hhplus.be.server.domain.order.controller.dto.OrderCreateRequest;
import kr.hhplus.be.server.domain.order.controller.dto.OrderCreateResponse;
import kr.hhplus.be.server.domain.order.mapper.OrderMapper;
import kr.hhplus.be.server.domain.order.testinstance.OrderTestInstance;
import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
@ExtendWith(MockitoExtension.class)
class OrderFacadeTest {

    @Mock OrderService orderService;
    @Mock CouponService couponService;
    @Mock UserService userService;
    @Mock OrderMapper orderMapper;
    @InjectMocks OrderFacade orderFacade;

    @Test
    void createOrder_withValidCoupon_appliesDiscountAndReturnsResponse() {
        // -- given --
        // 1) 요청 빌더로 테스트용 Request 생성
        OrderCreateRequest req = OrderCreateRequestBuilder.defaultRequest()
                .userId(1L)
                .item(100L, BigDecimal.valueOf(100), 2)
                .couponCode("COUPON10")
                .build();

        // 2) 의존 서비스가 돌려줄 더미 도메인/엔티티
        Users user = TestUsers.simpleUser(1L);
        CouponIssue coupon = TestCoupons.issueNew("COUPON10", BigDecimal.valueOf(10));
        Order domainOrder = TestOrders.domainOrder(req);
        Order discountedOrder = TestOrders.domainOrderWithDiscount(domainOrder, BigDecimal.valueOf(10));
        Order savedOrder = TestOrders.savedOrder(55L, discountedOrder);
        OrderCreateResponse expectedResponse = TestOrderResponses.from(savedOrder);

        // 3) Mockito 스텁 설정
        given(userService.getUser(req.userId())).willReturn(user);
        given(couponService.couponAppliedByOrder(req.couponCode())).willReturn(coupon);
        given(orderMapper.toDomain(req)).willReturn(domainOrder);
        // applyCoupon() 가 호출되어 domainOrder 내부의 각 line.discountPrice 가 변경됨
        given(orderService.createOrder(domainOrder)).willReturn(savedOrder);
        given(orderMapper.toResponse(savedOrder)).willReturn(expectedResponse);

        // -- when --
        OrderCreateResponse actual = orderFacade.createOrder(req);

        // -- then --
        assertThat(actual).isEqualTo(expectedResponse);

        // 할인 로직이 실제로 적용되었는지 확인 (선택적 검증)
        domainOrder.getOrderLines().forEach(line -> {
            BigDecimal expected = line.getOrderLinePrice()
                    .multiply(BigDecimal.ONE.subtract(BigDecimal.valueOf(0.1)))
                    .setScale(0, RoundingMode.HALF_UP);
            assertThat(line.getDiscountPrice()).isEqualTo(expected);
        });
    }

    @Test
    void createOrder_withoutCoupon_returnsFullPrice() {
        // -- given --
        OrderCreateRequest req = OrderCreateRequestBuilder.defaultRequest()
                .userId(2L)
                .item(200L, BigDecimal.valueOf(50), 1)
                .couponCode("")  // 쿠폰 없음
                .build();

        Users user = TestUsers.simpleUser(2L);
        Order domainOrder = TestOrders.domainOrder(req);
        Order savedOrder = TestOrders.savedOrder(99L, domainOrder);
        OrderCreateResponse expectedResponse = TestOrderResponses.from(savedOrder);

        given(userService.getUser(req.userId())).willReturn(user);
        given(couponService.couponAppliedByOrder(req.couponCode())).willReturn(null);
        given(orderMapper.toDomain(req)).willReturn(domainOrder);
        given(orderService.createOrder(domainOrder)).willReturn(savedOrder);
        given(orderMapper.toResponse(savedOrder)).willReturn(expectedResponse);

        // -- when --
        OrderCreateResponse actual = orderFacade.createOrder(req);

        // -- then --
        assertThat(actual).isEqualTo(expectedResponse);

        // 할인 적용 안 된 상태 확인
        domainOrder.getOrderLines().forEach(line ->
                assertThat(line.getDiscountPrice()).isNull()
        );
    }
}
