package kr.hhplus.be.server.domain.order.adapter.repository;

import kr.hhplus.be.server.domain.order.adapter.entity.OrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, String> {

}
