package kr.hhplus.be.server.domain.order.adapter.repository;

import kr.hhplus.be.server.domain.order.adapter.entity.OrderJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {

    //@EntityGraph(attributePaths = "orderLines")
    Optional<OrderJpaEntity> findById(Long id);

}
