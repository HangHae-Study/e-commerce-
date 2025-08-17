package kr.hhplus.be.server.domain.order.adapter.repository;

import kr.hhplus.be.server.domain.order.adapter.entity.OrderJpaEntity;
import kr.hhplus.be.server.domain.order.adapter.entity.OrderLineJpaEntity;
import kr.hhplus.be.server.domain.order.adapter.projection.BestSellingProductLineProjection;
import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.OrderLine;
import kr.hhplus.be.server.domain.order.application.repository.OrderLineRepository;
import kr.hhplus.be.server.domain.order.command.TopOrderProductCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Repository
@RequiredArgsConstructor
public class OrderLineJpaRepositoryAdapter implements OrderLineRepository {

    private final OrderLineJpaRepository orderLineJpaRepository;

    @Override
    public Optional<OrderLine> findById(String s) {
        return Optional.empty();
    }

    @Override
    public List<OrderLine> findAll() {
        return null;
    }

    @Override
    public OrderLine save(OrderLine orderLine) {
        return null;
    }

    @Override
    public void deleteById(String s) {

    }

    public List<OrderLine> findByOrderId(Order order){
        OrderJpaEntity entity = OrderJpaEntity.fromDomain(order);
        return orderLineJpaRepository.findOrderLineJpaEntitiesByOrder(entity).stream()
                .map(OrderLineJpaEntity::toDomain).toList();
    }

    public List<BestSellingProductLineProjection> findTop5ByOrderDtBetween(LocalDate start, LocalDate end){
        return orderLineJpaRepository.findTop5ByOrderDtBetween(start, end);
    }
}
