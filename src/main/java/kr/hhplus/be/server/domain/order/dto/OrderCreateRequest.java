package kr.hhplus.be.server.domain.order.dto;

import java.util.List;

public record OrderCreateRequest(
        String orderId,
        List<OrderItem> items,
        String couponCode
) {
    public record OrderItem(Long productLineId, Double linePrice, int quantity) {}
}
