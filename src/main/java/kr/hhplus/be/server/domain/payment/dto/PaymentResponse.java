package kr.hhplus.be.server.domain.payment.dto;

public record PaymentResponse(
        Long paymentId,
        String orderId,
        Double totalPrice,
        String paymentDt,
        String paymentStatus
) {}
