package kr.hhplus.be.server.domain.payment.command;

import kr.hhplus.be.server.domain.payment.application.Payment;

import java.math.BigDecimal;

public class PaymentCreateCommand {
    public record PaymentRequest(String orderCode) {}

    public record PaymentResponse(
            Long paymentId,
            Long orderId,
            BigDecimal totalPrice,
            String paymentDt,
            String paymentStatus
    ) {}

    public static PaymentResponse response(Payment payment){
        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getOrderId(),
                payment.getTotalPrice(),
                payment.getPaymentDt().toString(),
                payment.getStatus()
        );
    }
}
