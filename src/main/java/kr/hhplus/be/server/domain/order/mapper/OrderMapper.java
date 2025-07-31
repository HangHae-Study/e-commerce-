package kr.hhplus.be.server.domain.order.mapper;

import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.controller.dto.OrderCreateRequest;
import kr.hhplus.be.server.domain.order.controller.dto.OrderCreateResponse;
import kr.hhplus.be.server.domain.order.factory.OrderFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

// OrderMapper.java
@Component
public class OrderMapper {

    /** API DTO → 도메인용 파라미터 변환 */
    public Order toDomain(OrderCreateRequest req) {
        // Request 내부 아이템을 Factory 에 넘길 간단한 데이터 구조로 변환
        List<OrderItemData> items = req.items().stream()
                .map(i -> new OrderItemData(i.productLineId(), i.linePrice(), i.quantity()))
                .toList();
        // 팩토리 호출
        return OrderFactory.of(
                req.orderId(),
                req.orderCode(),
                req.userId(),
                req.totalPrice(),
                items
        );
    }

    /** 도메인 → API DTO */
    public OrderCreateResponse toResponse(Order order) {
        List<OrderCreateResponse.OrderResItem> lines = order.getOrderLines().stream()
                .map(line -> new OrderCreateResponse.OrderResItem(
                        line.getOrderLineId(),
                        line.getOrderLinePrice(),
                        line.getCouponYn(),
                        line.getDiscountPrice(),
                        line.getQuantity(),
                        line.getStatus()
                ))
                .toList();

        return new OrderCreateResponse(
                order.getOrderCode(),
                lines,
                order.getTotalPrice(),
                order.getOrderDt().toString(),
                order.getStatus()
        );
    }

    // 내부 변환용 DTO
    public static record OrderItemData(
            Long        productLineId,
            BigDecimal  price,
            int         quantity
    ) {}
}
