package kr.hhplus.be.server.domain.order.dto;

import java.util.List;

public record OrderCreateResponse(
        String orderId,
        List<OrderResItem> items,
        double totalPrice,
        String orderDt,
        String orderStatus
    ) {

    public record OrderResItem(
            Long productLineId,
            Double linePrice,
            String couponYN,
            Double discountPrice,
            int quantity,
            String orderStatus
    ) {}
}


