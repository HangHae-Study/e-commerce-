package kr.hhplus.be.server.domain.payment.application.service;

import kr.hhplus.be.server.domain.payment.application.Payment;
import kr.hhplus.be.server.domain.payment.application.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepo;

    @Transactional
    public Payment pay(Long userId, String orderId, BigDecimal totalPrice) {

        var payment = Payment.of(userId, orderId, totalPrice, "P_CMPL");

        return paymentRepo.save(payment);
    }
}
