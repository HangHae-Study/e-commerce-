package kr.hhplus.be.server.domain.order.application.repository;

import kr.hhplus.be.server.common.RepositoryPort;
import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.OrderLine;

import java.util.List;

public interface OrderLineRepository extends RepositoryPort<String, OrderLine> {
    public List<OrderLine> findByOrderId(Order order);
}
