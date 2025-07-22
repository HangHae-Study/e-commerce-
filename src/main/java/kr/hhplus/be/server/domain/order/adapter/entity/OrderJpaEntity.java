package kr.hhplus.be.server.domain.order.adapter.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.OrderLine;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String id;

    private Long userId;

    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderLineJpaEntity> orderLines = new ArrayList<>();

    private LocalDateTime orderDt;
    private String orderStatus;
    private LocalDateTime updateDt;


    public Order toDomain() {
        List<OrderLine> lines = orderLines.stream().map(OrderLineJpaEntity::toDomain).toList();
        return new Order(id, userId, totalPrice, lines, orderDt, orderStatus, updateDt);
    }

    public static OrderJpaEntity fromDomain(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.userId = order.getUserId();
        entity.orderLines = order.getOrderLines().stream()
                .map(line -> OrderLineJpaEntity.fromDomain(line, entity))
                .toList();
        return entity;
    }
}
