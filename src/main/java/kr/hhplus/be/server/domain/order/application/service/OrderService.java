package kr.hhplus.be.server.domain.order.application.service;

import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.OrderLine;
import kr.hhplus.be.server.domain.order.application.dto.OrderCreateRequest;
import kr.hhplus.be.server.domain.order.application.repository.OrderLineRepository;
import kr.hhplus.be.server.domain.order.application.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;

    public Order getOrder(String orderId){
        Order order =  orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("유효하지 않은 주문 정보입니다."));

        List<OrderLine> lines = orderLineRepository.findByOrderId(order);
        return order.toBuilder()
                .orderLines(lines)
                .build();
    }

    public Order createOrder(Order ordReq){
        Order order = orderRepository.save(ordReq);
        List<OrderLine> lines = orderLineRepository.findByOrderId(order);
        return order.toBuilder()
                .orderLines(lines)
                .build();
    }

    public Order createOrder(Order nOrder, CouponIssue ci){
        nOrder.getOrderLines().forEach(line -> line.applyCoupon(ci));

        BigDecimal newTotal = nOrder.getOrderLines().stream()
                .map(OrderLine::getDiscountPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order updated = Order.builder()
                .orderId(nOrder.getOrderId())
                .userId(nOrder.getUserId())
                .totalPrice(newTotal)
                .orderLines(nOrder.getOrderLines())
                .orderDt(nOrder.getOrderDt())
                .status(nOrder.getStatus())
                .updateDt(LocalDateTime.now())
                .build();
        return createOrder(updated);
    }


    public void orderComplete(Order order) {
        // O_MAKE -> O_CMPL;
        order.complete();
        order.getOrderLines().forEach(OrderLine::complete);
    }

    public Order orderRequested(OrderCreateRequest req, CouponIssue couponIssue) {
        Order newOrder = Order.create(req);

        if(couponIssue != null){
            return createOrder(newOrder, couponIssue);
        }else{
            return createOrder(newOrder);
        }
    }
}
