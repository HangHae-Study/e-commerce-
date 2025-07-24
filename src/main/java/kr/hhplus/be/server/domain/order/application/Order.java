package kr.hhplus.be.server.domain.order.application;

import kr.hhplus.be.server.domain.order.application.dto.OrderCreateRequest;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class Order {
    private String orderId;
    private Long userId;
    private BigDecimal totalPrice;
    @Singular
    private List<OrderLine> orderLines;
    private LocalDateTime orderDt;
    private String status;
    private LocalDateTime updateDt;

    public static Order create(OrderCreateRequest req){
        List<OrderLine> ol = req.items().stream()
                .map(a -> OrderLine.create(req, a)).toList();

        return Order.builder()
                .orderId(req.orderId())
                .userId(req.userId())
                .totalPrice(req.totalPrice())
                .orderLines(ol)
                .orderDt(LocalDateTime.now())
                .status("O_MAKE")
                .updateDt(LocalDateTime.now())
                .build();
    }

    public void complete() {
        setStatus("O_CMPL");
    }
}
