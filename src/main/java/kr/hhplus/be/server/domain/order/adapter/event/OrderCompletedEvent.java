package kr.hhplus.be.server.domain.order.adapter.event;

import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.product.command.ProductRankingDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record OrderCompletedEvent(
        String eventId,
        Long orderId,
        String orderCode,
        Long userId,
        LocalDate orderDate,
        List<ProductRankingDto.ProductItemForRank> items
) {
    public OrderCompletedEvent(Order order) {
        this(
                UUID.randomUUID().toString(),
                order.getOrderId(),
                order.getOrderCode(),
                order.getUserId(),
                order.getOrderDt().toLocalDate(),
                order.getOrderLines().stream()
                        .map(ord -> new ProductRankingDto.ProductItemForRank(ord.getProductLineId(), ord.getQuantity()))
                        .toList()
        );
    }
}

