package kr.hhplus.be.server.domain.order.adapter.repository;

import kr.hhplus.be.server.domain.order.adapter.entity.OrderJpaEntity;
import kr.hhplus.be.server.domain.order.adapter.entity.OrderLineJpaEntity;
import kr.hhplus.be.server.domain.order.adapter.projection.BestSellingProductLineProjection;
import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.repository.OrderLineRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderLineJpaRepository extends JpaRepository<OrderLineJpaEntity, Long> {

    List<OrderLineJpaEntity> findOrderLineJpaEntitiesByOrder(OrderJpaEntity order);

    // 인덱스 테스트를 위한 함수
    @Query(value = """
        SELECT
          ol.product_line_id   AS productLineId,
          SUM(ol.quantity)     AS totalQuantity
        FROM order_lines ol
        WHERE ol.order_dt BETWEEN :start AND :end
          AND ol.status = 'O_CMPL'
        GROUP BY ol.product_line_id
        ORDER BY totalQuantity DESC
        LIMIT 5
        """,
            nativeQuery = true)
    List<BestSellingProductLineProjection> findTop5ByOrderDtBetween(
            @Param("start") LocalDate start,
            @Param("end")   LocalDate end
    );
}
