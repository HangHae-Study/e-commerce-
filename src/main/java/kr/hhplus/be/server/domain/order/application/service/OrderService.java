package kr.hhplus.be.server.domain.order.application.service;

import kr.hhplus.be.server.domain.order.adapter.projection.BestSellingProductLineProjection;
import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.OrderLine;
import kr.hhplus.be.server.domain.order.application.repository.OrderLineRepository;
import kr.hhplus.be.server.domain.order.application.repository.OrderRepository;
import kr.hhplus.be.server.domain.order.command.TopOrderProductCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;

    public Order createOrder(Order order) {
        Order newOrder = orderRepository.save(order);

        return newOrder;
    }

    @Transactional
    public Order getOrder(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("올바르지 않은 주문 입니다"));
        return order;
    }

    @Transactional
    public Order getOrderByCode(String orderCode){
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new NoSuchElementException("올바르지 않은 주문 입니다"));
        return order;
    }


    @Transactional
    public Order orderComplete(Order order) {
        order.complete();
        for (OrderLine orderLine : order.getOrderLines()) {
            orderLine.complete();
        }
        return orderRepository.save(order);
    }

    @Transactional
    public void orderFailed(Order order) {
        order.fail();
        for (OrderLine orderLine : order.getOrderLines()) {
            orderLine.fail();
        }
        orderRepository.save(order);
    }

    public List<TopOrderProductCommand.TopOrderProductResponse> getTopOrderProduct(LocalDate start, LocalDate end){
        return orderLineRepository.findTop5ByOrderDtBetween(start, end)
                .stream().map(
                        v -> {
                            return new TopOrderProductCommand.TopOrderProductResponse(
                                v.getProductLineId(), v.getTotalQuantity()
                            );
                        }).toList();
    }
}
