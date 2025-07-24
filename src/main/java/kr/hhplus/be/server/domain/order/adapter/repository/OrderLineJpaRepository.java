package kr.hhplus.be.server.domain.order.adapter.repository;

import kr.hhplus.be.server.domain.order.adapter.entity.OrderJpaEntity;
import kr.hhplus.be.server.domain.order.adapter.entity.OrderLineJpaEntity;
import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.repository.OrderLineRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderLineJpaRepository extends JpaRepository<OrderLineJpaEntity, Long> {

    List<OrderLineJpaEntity> findOrderLineJpaEntitiesByOrder(OrderJpaEntity order);
}
