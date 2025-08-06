package kr.hhplus.be.server.domain.payment.application.dto;

import java.math.BigDecimal;

public record PaymentResponse(
        Long paymentId,
        String orderId,
        BigDecimal totalPrice,
        String paymentDt,
        String paymentStatus
) {}
