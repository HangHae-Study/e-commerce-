package kr.hhplus.be.server.domain.payment.adapter.repository;

import kr.hhplus.be.server.domain.payment.adapter.entity.PaymentJpaEntity;
import kr.hhplus.be.server.domain.payment.application.Payment;
import kr.hhplus.be.server.domain.payment.application.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class PaymentJapRepositoryAdapter implements PaymentRepository {
    private final PaymentJpaRepository jpaRepo;

    @Override
    public Optional<Payment> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<Payment> findAll() {
        return null;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentJpaEntity entity = jpaRepo.save(PaymentJpaEntity.fromDomain(payment));
        return entity.toDomain();
    }

    @Override
    public void deleteById(Long aLong) {

    }
}
