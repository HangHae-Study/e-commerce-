package kr.hhplus.be.server.domain.order.controller.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderCreateRequest(
        Long orderId,
        String orderCode,
        Long userId,
        BigDecimal totalPrice,
        List<OrderItem> items,
        String couponCode
) {
    public record OrderItem(Long productLineId, BigDecimal linePrice, int quantity) {}
}
