package kr.hhplus.be.server.domain.order.application;

import jakarta.annotation.PostConstruct;
import kr.hhplus.be.server.domain.order.controller.dto.OrderCreateRequest;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class Order {
    private final Long orderId;
    private final String orderCode;
    private final Long userId;
    private final BigDecimal totalPrice;
    private final List<OrderLine> orderLines;
    private LocalDateTime orderDt;
    private String status;
    private LocalDateTime updateDt;


    public void complete() {
        if (!"O_MAKE".equals(status)) throw new IllegalStateException("주문 완료할 수 없는 상태입니다.");

        updateDt = LocalDateTime.now();
        setStatus("O_CMPL");
    }

    @PostConstruct
    private void validate() {
        if (orderLines.isEmpty()) throw new IllegalStateException("주문 항목이 비어있습니다.");
        BigDecimal sum = orderLines.stream()
                .map(OrderLine::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (!sum.equals(totalPrice))
            throw new IllegalStateException("총합이 일치하지 않습니다.");
    }
}
