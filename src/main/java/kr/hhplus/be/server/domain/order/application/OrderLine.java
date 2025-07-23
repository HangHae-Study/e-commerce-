package kr.hhplus.be.server.domain.order.application;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderLine {
    private final Long orderLineId;
    private final String orderId;

    private final Long userId;
    private final Long productId;
    private final Long productLineId;

    private final BigDecimal orderLinePrice;
    private final int quantity;

    private final String couponYn;
    private final String couponCode;
    private final BigDecimal discountPrice;

    private final String status;
    private final LocalDateTime orderDt;
    private final LocalDateTime updateDt;

    public BigDecimal getSubtotal() {
        return orderLinePrice.multiply(new BigDecimal(quantity));
    }

}

