package kr.hhplus.be.server.domain.order.adapter.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.OrderLine;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
public class OrderJpaEntity {
    @Id
    private String orderId;

    private Long userId;

    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderLineJpaEntity> orderLines = new ArrayList<>();

    private LocalDateTime orderDt;
    private String status;

    @UpdateTimestamp
    @Column(
            name = "update_dt",
            nullable = false,
            columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
    )
    private LocalDateTime updateDt;


    public Order toDomain() {
        //List<OrderLine> lines = orderLines.stream().map(OrderLineJpaEntity::toDomain).toList();
        return Order.builder()
                .orderId(orderId)
                .userId(userId)
                .totalPrice(totalPrice)
                //.orderLines(lines)
                .orderDt(orderDt)
                .status(status)
                .updateDt(updateDt)
                .build();
    }

    public static OrderJpaEntity fromDomain(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.orderId = order.getOrderId();
        entity.totalPrice = order.getTotalPrice();
        entity.userId = order.getUserId();
        entity.orderLines = order.getOrderLines().stream()
                .map(line -> OrderLineJpaEntity.fromDomain(line, entity))
                .toList();
        entity.orderDt = LocalDateTime.now();
        entity.status = order.getStatus();
        return entity;
    }
}
