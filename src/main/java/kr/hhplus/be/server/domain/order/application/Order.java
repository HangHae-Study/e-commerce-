package kr.hhplus.be.server.domain.order.application;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class Order {
    private final String orderId;
    private final Long userId;
    private final BigDecimal totalPrice;
    private final List<OrderLine> orderLines;
    private final LocalDateTime orderDt;
    private final String status;
    private final LocalDateTime updateDt;
}
