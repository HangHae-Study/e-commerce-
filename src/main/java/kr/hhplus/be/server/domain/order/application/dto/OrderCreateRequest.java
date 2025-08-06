package kr.hhplus.be.server.domain.order.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderCreateRequest(
        String orderId,
        Long userId,
        BigDecimal totalPrice,
        List<OrderItem> items,
        String couponCode
) {
    public record OrderItem(Long productLineId, BigDecimal linePrice, int quantity) {}
}
