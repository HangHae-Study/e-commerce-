package kr.hhplus.be.server.domain.order.application.facade;

import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import kr.hhplus.be.server.domain.coupon.application.service.CouponService;
import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.controller.dto.OrderCreateRequest;
import kr.hhplus.be.server.domain.order.controller.dto.OrderCreateResponse;
import kr.hhplus.be.server.domain.order.application.service.OrderService;
import kr.hhplus.be.server.domain.order.mapper.OrderMapper;
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
    private final OrderMapper orderMapper;

    public OrderCreateResponse createOrder(OrderCreateRequest req) {
        Users user = userService.getUser(req.userId());
        CouponIssue couponIssue = couponService.couponAppliedByOrder(user.getUserId(), req.couponCode());

        Order order = orderMapper.toDomain(req);
        if (couponIssue != null) {
            order.getOrderLines()
                    .forEach(line -> line.applyCoupon(couponIssue.getDiscountRate()));
        }

        // 3) 저장
        Order saved = orderService.createOrder(order);

        // 4) 응답 DTO 변환
        return orderMapper.toResponse(saved);
    }
}
