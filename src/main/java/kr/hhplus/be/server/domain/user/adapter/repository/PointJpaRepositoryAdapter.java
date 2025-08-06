package kr.hhplus.be.server.domain.user.adapter.repository;

import kr.hhplus.be.server.domain.user.adapter.entity.PointJpaEntity;
import kr.hhplus.be.server.domain.user.application.dto.PointDao;
import kr.hhplus.be.server.domain.user.application.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PointJpaRepositoryAdapter implements PointRepository {

    private final PointJpaRepository pointJpaRepository;

    @Override
    public Optional<PointDao> findByUserId(Long userId) {
        return  pointJpaRepository.findPointJpaRepositoryByUserId(userId)
                        .map(PointJpaEntity::toDao);
    }

    @Override
    public Optional<PointDao> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<PointDao> findAll() {
        return List.of();
    }

    @Override
    public PointDao save(PointDao point) {
        return null;
    }

    @Override
    public void deleteById(Long aLong) {

    }
}
