package kr.hhplus.be.server.domain.payment.adapter.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.payment.application.Payment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor
public class PaymentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "payment_dt", nullable = false)
    private LocalDateTime paymentDt;

    //@Enumerated(EnumType.STRING)
    @Column(name = "status")//, columnDefinition = "ENUM('P_CMPL','P_FAIL')")
    private String status;

    @UpdateTimestamp
    @Column(
            name = "update_dt",
            nullable = false,
            columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
    )
    private LocalDateTime updateDt;


    public static PaymentJpaEntity fromDomain(Payment p) {
        var e = new PaymentJpaEntity();
        e.paymentId = p.getPaymentId();
        e.userId = p.getUserId();
        e.orderId = p.getOrderId();
        e.totalPrice = p.getTotalPrice();
        e.paymentDt = p.getPaymentDt();
        e.status = p.getStatus();
        e.updateDt = p.getPaymentDt();
        return e;
    }

    public Payment toDomain() {
        return Payment.builder()
                .paymentId(paymentId)
                .userId(userId)
                .orderId(orderId)
                .totalPrice(totalPrice)
                .paymentDt(paymentDt)
                .status(status)
                .updateDt(String.valueOf(updateDt))
                .build();
    }
}