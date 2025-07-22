package kr.hhplus.be.server.domain.order.application.repository;

import kr.hhplus.be.server.common.RepositoryPort;
import kr.hhplus.be.server.domain.order.application.Order;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends RepositoryPort<String, Order> {

}
