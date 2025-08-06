package kr.hhplus.be.server.domain.payment.application;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder()
public class Payment{
    private Long paymentId;
    private Long userId;
    private Long orderId;
    private BigDecimal totalPrice;
    private LocalDateTime paymentDt;
    private String status;
    private String updateDt;

    public static Payment of(Long userId, Long orderId, BigDecimal totalPrice, String status) {
        return Payment.builder()
                .userId(userId)
                .orderId(orderId)
                .totalPrice(totalPrice)
                .paymentDt(LocalDateTime.now())
                .status(status)
                .build();
    }
}
