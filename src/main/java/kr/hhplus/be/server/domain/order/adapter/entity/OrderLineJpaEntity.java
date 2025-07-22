package kr.hhplus.be.server.domain.order.adapter.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.order.application.OrderLine;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_lines")
@Getter
@NoArgsConstructor
public class OrderLineJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Long productLineId;

    private int quantity;
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderJpaEntity order;

    public OrderLine toDomain() {
        return new OrderLine(id, productId, quantity, price);
    }

    public static OrderLineJpaEntity fromDomain(OrderLine line, OrderJpaEntity order) {
        OrderLineJpaEntity entity = new OrderLineJpaEntity();
        entity.productId = line.getProductLineId();
        entity.quantity = line.getQuantity();
        entity.price = line.getPrice();
        entity.order = order;
        return entity;
    }

}

