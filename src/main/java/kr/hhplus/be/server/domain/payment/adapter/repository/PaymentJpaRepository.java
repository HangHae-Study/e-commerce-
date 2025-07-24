package kr.hhplus.be.server.domain.payment.adapter.repository;

import kr.hhplus.be.server.domain.payment.adapter.entity.PaymentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Long> {
}
