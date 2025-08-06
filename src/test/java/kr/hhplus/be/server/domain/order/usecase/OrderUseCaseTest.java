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
import kr.hhplus.be.server.domain.order.testinstance.OrderUseCaseInstance;
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

    OrderUseCaseInstance factory;
    @BeforeEach
    void setup(){
        factory = OrderUseCaseInstance.defaultRequest();
    }

    @Test
    void 주문_생성_요청_성공() {
        // 1) 요청 빌더로 테스트용 Request 생성
        OrderCreateRequest req = factory
                .userId(2L)
                .item(200L, BigDecimal.valueOf(50), 1)
                .couponCode("")
                .build();

        // 2) 의존 서비스 반환 도메인/엔티티
        Users user = factory.userInFacade();
        Order domainOrder = factory.orderInFacade();
        Order savedOrder = factory.orderSavedInFacade(99L, domainOrder);
        OrderCreateResponse expectedResponse = factory.orderResponseInFacade(savedOrder);

        given(userService.getUser(req.userId())).willReturn(user);
        given(couponService.couponAppliedByOrder(user.getUserId(), req.couponCode())).willReturn(null);
        given(orderMapper.toDomain(req)).willReturn(domainOrder);
        given(orderService.createOrder(domainOrder)).willReturn(savedOrder);
        given(orderMapper.toResponse(savedOrder)).willReturn(expectedResponse);

        // -- when --
        OrderCreateResponse actual = orderFacade.createOrder(req);

        // -- then --
        assertThat(actual).isEqualTo(expectedResponse);

        // 할인 적용 안 된 상태 확인
        domainOrder.getOrderLines().forEach(line ->{
            assertThat(line.getDiscountPrice()).isNull();
        });
    }

    @Test
    void 주문_생성_요청_할인_적용_성공() {
        // 1) 요청 빌더로 테스트용 Request 생성
        OrderCreateRequest req = factory
                .userId(1L)
                .item(100L, BigDecimal.valueOf(100), 2)
                .item(101L, BigDecimal.valueOf(200), 1)
                .couponCode("COUPON10")
                .build();

        // 2) 의존 서비스 반환 도메인/엔티티
        Users user = factory.userInFacade();
        CouponIssue coupon = factory.couponInFacade(BigDecimal.valueOf(10));
        Order domainOrder = factory.orderInFacade();
        Order discountedOrder = factory.orderDiscountedInFacade(domainOrder, coupon.getDiscountRate());
        Order savedOrder = factory.orderSavedInFacade(55L, discountedOrder);
        OrderCreateResponse expectedResponse = factory.orderResponseInFacade(savedOrder);

        // 3) Mockito 스텁 설정
        given(userService.getUser(req.userId())).willReturn(user);
        given(couponService.couponAppliedByOrder(user.getUserId(), req.couponCode())).willReturn(coupon);
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
                    .multiply(BigDecimal.ONE.subtract(coupon.getDiscountRate().movePointLeft(2)))
                    .setScale(0, RoundingMode.HALF_UP);
            assertThat(line.getDiscountPrice()).isEqualTo(expected);
        });
    }

    @Test
    void 주문_생성_요청_올바르지않은_사용자() {
        // given
        OrderCreateRequest req = factory
                .userId(1L)
                .item(100L, BigDecimal.valueOf(100), 1)
                .couponCode("")
                .build();
        given(userService.getUser(req.userId()))
                .willThrow(new NoSuchElementException("올바르지 않은 사용자 입니다."));

        // when / then
        assertThatThrownBy(() -> orderFacade.createOrder(req))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("올바르지 않은 사용자 입니다.");

        // verify 호출 흐름
        verify(userService).getUser(req.userId());
        // 이후에는 아무 호출도 일어나지 않아야 함
        verifyNoInteractions(couponService, orderMapper, orderService);
    }

    @Test
    void 주문_생성_요청_올바르지않은_쿠폰() {
        // given
        OrderCreateRequest req = factory
                .userId(1L)
                .item(100L, BigDecimal.valueOf(100), 1)
                .couponCode("BADCODE")
                .build();

        Users user = factory.userInFacade();

        given(userService.getUser(req.userId())).willReturn(user);
        given(couponService.couponAppliedByOrder(user.getUserId(), req.couponCode()))
                .willThrow(new NoSuchElementException("유효하지 않은 쿠폰 입니다."));

        // when / then
        assertThatThrownBy(() -> orderFacade.createOrder(req))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("유효하지 않은 쿠폰 입니다.");

        // verify 호출 흐름
        verify(userService).getUser(req.userId());
        verify(couponService).couponAppliedByOrder(user.getUserId(), req.couponCode());
        // 이후에는 도메인 매핑이나 저장 로직이 호출되지 않아야 함
        verifyNoInteractions(orderMapper, orderService);
    }




}
