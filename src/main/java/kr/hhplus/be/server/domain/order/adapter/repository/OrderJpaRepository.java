package kr.hhplus.be.server.domain.order.adapter.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.order.adapter.entity.OrderJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {

    Optional<OrderJpaEntity> findByOrderCode(String code);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select o from OrderJpaEntity o where o.orderId = :orderId")
    Optional<OrderJpaEntity> findByIdWithLock(@Param("orderId") Long id);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select o from OrderJpaEntity o where o.orderCode = :code")
    Optional<OrderJpaEntity> findByOrderCodeWithLock(@Param("code") String code);
}
