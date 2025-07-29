package kr.hhplus.be.server.domain.order.application.facade;

import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import kr.hhplus.be.server.domain.coupon.application.service.CouponService;
import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.dto.OrderCreateRequest;
import kr.hhplus.be.server.domain.order.application.dto.OrderCreateResponse;
import kr.hhplus.be.server.domain.order.application.service.OrderService;
import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final CouponService couponService;
    private final UserService userService;

    public OrderCreateResponse createOrder(OrderCreateRequest req) {
        Users user = userService.getUser(req.userId());
        CouponIssue couponIssue = couponService.couponAppliedByOrder(req.couponCode());
        Order createdOrder = orderService.orderRequested(req, couponIssue);

        List<OrderCreateResponse.OrderResItem> items = createdOrder.getOrderLines().stream()
                .map(
                    item -> new OrderCreateResponse.OrderResItem(
                            item.getProductLineId(),
                            item.getOrderLinePrice(),
                            item.getCouponYn(),
                            item.getDiscountPrice(),
                            item.getQuantity(),
                            item.getStatus()
                    )
                ).toList();

        OrderCreateResponse orderRes = new OrderCreateResponse(
                createdOrder.getOrderId(),
                items,
                createdOrder.getTotalPrice(),
                createdOrder.getOrderDt().toString(),
                createdOrder.getStatus()
        );

        return orderRes;

    }
}
