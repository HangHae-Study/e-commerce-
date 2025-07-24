package kr.hhplus.be.server.domain.order.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderCreateResponse(
        String orderId,
        List<OrderResItem> items,
        BigDecimal totalPrice,
        String orderDt,
        String orderStatus
    ) {

    public record OrderResItem(
            Long productLineId,
            BigDecimal linePrice,
            String couponYN,
            BigDecimal discountPrice,
            int quantity,
            String orderStatus
    ) {}
}


