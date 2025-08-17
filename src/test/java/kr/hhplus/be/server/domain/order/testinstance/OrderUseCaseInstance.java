package kr.hhplus.be.server.domain.order.testinstance;

import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.OrderLine;
import kr.hhplus.be.server.domain.order.controller.dto.OrderCreateRequest;
import kr.hhplus.be.server.domain.order.controller.dto.OrderCreateResponse;
import kr.hhplus.be.server.domain.order.command.mapper.OrderMapper;
import kr.hhplus.be.server.domain.user.application.Users;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderUseCaseInstance {

    private Long userId = 1L;
    private String orderCode = "ORD-TEST";
    private final List<OrderCreateRequest.OrderItem> items = new ArrayList<>();

    private BigDecimal totalPrice;

    private String couponCode = "";

    public static OrderUseCaseInstance defaultRequest() {
        return new OrderUseCaseInstance();
    }

    public OrderUseCaseInstance userId(Long id) {
        this.userId = id; return this;
    }
    public OrderUseCaseInstance item(Long productLineId, BigDecimal linePrice, int qty) {
        this.items.add(new OrderCreateRequest.OrderItem(productLineId, linePrice, qty));
        this.totalPrice = this.items.stream()
                .map(i -> i.linePrice().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return this;
    }

    public OrderUseCaseInstance totalPrice(BigDecimal price){
        this.totalPrice = price;
        return this;
    }

    public OrderUseCaseInstance couponCode(String code) {
        this.couponCode = code; return this;
    }

    public OrderCreateRequest build() {
        return new OrderCreateRequest(null, orderCode, userId, totalPrice, items, couponCode);
    }

    // --- 각 도메인 객체 생성
    public Users userInFacade(){
        return Users.builder()
                .userId(this.userId)
                .build();
    }

    public CouponIssue couponInFacade(BigDecimal discountRate){
        return CouponIssue.builder()
                .couponCode(couponCode)
                .discountRate(discountRate)
                .build();
    }

    public Order orderInFacade(){
        List<OrderLine> lines = items.stream()
                .map(item -> OrderLine.builder()
                        .orderLinePrice(item.linePrice())
                        .quantity(item.quantity())
                        .orderDt(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());
        return Order.builder()
                .userId(userId)
                .totalPrice(lines.stream()
                        .map(OrderLine::getSubtotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .orderLines(lines)
                .orderDt(LocalDateTime.now())
                .status("O_MAKE")
                .build();
    }

    public Order orderDiscountedInFacade(Order ord, BigDecimal discountRate){
        ord.getOrderLines()
                .forEach(line -> line.applyCoupon(discountRate));
        return ord;
    }

    public Order orderSavedInFacade(Long orderId, Order ord){
        return Order.builder()
                .orderId(orderId)
                .status(ord.getStatus())
                .build();
    }

    public OrderCreateResponse orderResponseInFacade(Order ord){
        OrderMapper om = new OrderMapper();
        return om.toResponse(ord);
    }


}
