package kr.hhplus.be.server.domain.order.factory;

import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.OrderLine;
import kr.hhplus.be.server.domain.order.mapper.OrderMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// OrderFactory.java
public class OrderFactory {
    public static Order of(
            Long orderId,
            String orderCode,
            Long   userId,
            BigDecimal totalPrice,
            List<OrderMapper.OrderItemData> items
    ) {
        // OrderLine 도메인 객체 생성
        List<OrderLine> lines = items.stream()
                .map(data -> OrderLine.builder()
                        .orderId(orderId)
                        .userId(userId)
                        .productLineId(data.productLineId())
                        .orderLinePrice(data.price())
                        .quantity(data.quantity())
                        .orderDt(LocalDateTime.now())
                        .status("O_MAKE")
                        .updateDt(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());

        return Order.builder()
                .orderId(orderId)
                .orderCode(orderCode)
                .userId(userId)
                .totalPrice(totalPrice)
                .orderLines(lines)
                .orderDt(LocalDateTime.now())
                .status("O_MAKE")
                .updateDt(LocalDateTime.now())
                .build();

    }
}

