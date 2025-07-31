package kr.hhplus.be.server.domain.order.application.repository;

import kr.hhplus.be.server.common.RepositoryPort;
import kr.hhplus.be.server.domain.order.application.Order;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface OrderRepository extends RepositoryPort<Long, Order> {
    Optional<Order> findByOrderCode(String code);
}
