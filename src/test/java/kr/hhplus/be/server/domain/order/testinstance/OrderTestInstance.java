package kr.hhplus.be.server.domain.order.testinstance;


import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.OrderLine;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class OrderTestInstance {

    /** status="O_MAKE"인 기본 OrderLine */
    public static OrderLine simpleOrderLine() {
        return OrderLine.builder()
                .orderLineId(1L)
                .orderId(1L)
                .userId(1L)
                .productId(100L)
                .productLineId(1000L)
                .orderLinePrice(new BigDecimal("100"))
                .quantity(2)
                .status("O_MAKE")
                .orderDt(LocalDateTime.now())
                .updateDt(LocalDateTime.now())
                .build();
    }

    /** 할인율 20%를 적용한 OrderLine */
    public static OrderLine discountedOrderLine() {
        OrderLine line = simpleOrderLine();
        line.applyCoupon(new BigDecimal("20")); // 20% 할인
        return line;
    }

    /** 정상적인 단일 Order */
    public static Order simpleOrder() {
        OrderLine line = simpleOrderLine();
        BigDecimal total = line.getSubtotal();
        return Order.builder()
                .orderId(1L)
                .orderCode("CODE-1")
                .userId(1L)
                .totalPrice(total)
                .orderLines(List.of(line))
                .status("O_MAKE")
                .orderDt(LocalDateTime.now())
                .updateDt(LocalDateTime.now())
                .build();
    }

    /** 이미 complete() 호출된 Order */
    public static Order completedOrder() {
        Order o = simpleOrder();
        o.complete();
        return o;
    }

    /** 총합이 틀린 Order */
    public static Order mismatchedTotalOrder() {
        OrderLine line = simpleOrderLine();
        return Order.builder()
                .orderId(2L)
                .orderCode("CODE-2")
                .userId(1L)
                .totalPrice(line.getSubtotal().add(BigDecimal.ONE)) // 의도적으로 오차 발생
                .orderLines(List.of(line))
                .status("O_MAKE")
                .orderDt(LocalDateTime.now())
                .updateDt(LocalDateTime.now())
                .build();
    }

    /** 주문 항목이 없는 Order */
    public static Order emptyLinesOrder() {
        return Order.builder()
                .orderId(3L)
                .orderCode("CODE-3")
                .userId(1L)
                .totalPrice(BigDecimal.ZERO)
                .orderLines(Collections.emptyList())
                .status("O_MAKE")
                .orderDt(LocalDateTime.now())
                .updateDt(LocalDateTime.now())
                .build();
    }

    /** DB저장 후 검증용*/
    public static OrderLine persistedOrderLine() {
        return OrderLine.builder()
                .orderLineId(10L)
                .orderId(100L)
                .userId(50L)
                .productId(200L)
                .productLineId(300L)
                .orderLinePrice(new BigDecimal("500"))
                .quantity(2)
                .status("O_MAKE")
                .orderDt(LocalDateTime.now())
                .updateDt(LocalDateTime.now())
                .build();
    }

    public static Order persistedOrder() {
        OrderLine line  = persistedOrderLine();
        BigDecimal total = line.getSubtotal();
        return Order.builder()
                .orderId(100L)
                .orderCode("ORD-100")
                .userId(50L)
                .totalPrice(total)
                .orderLines(List.of(line))
                .orderDt(LocalDateTime.now())
                .status("O_MAKE")
                .updateDt(LocalDateTime.now())
                .build();
    }
}
