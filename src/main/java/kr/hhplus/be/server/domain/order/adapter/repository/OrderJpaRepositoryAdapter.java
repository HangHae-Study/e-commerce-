package kr.hhplus.be.server.domain.order.adapter.repository;

import kr.hhplus.be.server.domain.order.adapter.entity.OrderJpaEntity;
import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderJpaRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    @Override
    public Optional<Order> findById(Long orderId) {
        return jpaRepository.findById(orderId)
                .map(OrderJpaEntity::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return jpaRepository.findAll().stream()
                .map(OrderJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = OrderJpaEntity.fromDomain(order);
        OrderJpaEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public void deleteById(Long orderId) {
        jpaRepository.deleteById(orderId);
    }

    @Override
    public Optional<Order> findByOrderCode(String code) {
        //return jpaRepository.findByOrderCode(code).map(OrderJpaEntity::toDomain);
        // step 09 : 낙관적 락으로 변경
        return jpaRepository.findByOrderCodeWithLock(code).map(OrderJpaEntity::toDomain);
    }
}
