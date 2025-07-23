package kr.hhplus.be.server.domain.order.adapter.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.order.application.OrderLine;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_lines")
@Getter
@NoArgsConstructor
public class OrderLineJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderLineId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderJpaEntity order;

    private Long userId;
    private Long productId;
    private Long productLineId;

    private BigDecimal orderLinePrice;
    private int quantity;

    private String couponYn;
    private String couponCode;
    private BigDecimal disCountPrice;

    private String status;
    private LocalDateTime orderDt;
    private LocalDateTime updateDt;

    public OrderLine toDomain() {
        return OrderLine.builder()
                .orderLineId(orderLineId)
                .orderId(order.getOrderId())
                .userId(userId)
                .productId(productId)
                .productLineId(productLineId)
                .quantity(quantity)
                .orderLinePrice(orderLinePrice)
                .couponYn(couponYn)
                .couponCode(couponCode)
                .discountPrice(disCountPrice)
                .status(status)
                .orderDt(orderDt)
                .updateDt(updateDt)
                .build();
    }

    public static OrderLineJpaEntity fromDomain(OrderLine line, OrderJpaEntity order) {
        OrderLineJpaEntity entity = new OrderLineJpaEntity();
        entity.order = order;
        if(line.getOrderLineId() != null){
            entity.orderLineId = line.getOrderLineId();
        }

        entity.userId         = line.getUserId();
        entity.productId      = line.getProductId();
        entity.productLineId  = line.getProductLineId();
        entity.orderLinePrice = line.getOrderLinePrice();
        entity.quantity       = line.getQuantity();

        entity.couponYn       = line.getCouponYn();
        entity.couponCode     = line.getCouponCode();
        entity.disCountPrice  = line.getDiscountPrice();

        entity.status         = line.getStatus();
        entity.orderDt        = line.getOrderDt();
        entity.updateDt       = line.getUpdateDt();

        return entity;
    }


}

