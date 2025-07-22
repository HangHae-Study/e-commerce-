package kr.hhplus.be.server.domain.order.adapter.repository;

import kr.hhplus.be.server.domain.order.application.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderJpaRepositoryAdapter implements OrderRepository {
    private final OrderJpaRepository jpaRepository;

    @Override
    public Optional findById(Object o) {
        return Optional.empty();
    }

    @Override
    public List findAll() {
        return List.of();
    }

    @Override
    public Object save(Object o) {
        return null;
    }

    @Override
    public void deleteById(Object o) {

    }
}
