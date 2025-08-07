package kr.hhplus.be.server.domain.user.adapter.repository;

import kr.hhplus.be.server.domain.user.adapter.entity.PointRecordJpaEntity;
import kr.hhplus.be.server.domain.user.application.dto.PointRecordDao;
import kr.hhplus.be.server.domain.user.application.repository.PointRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PointRecordJpaRepositoryAdapter implements PointRecordRepository {
    private final PointRecordJpaRepository pointRecordJpaRepository;

    @Override
    public Optional<PointRecordDao> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<PointRecordDao> findAll() {
        return pointRecordJpaRepository.findAll()
                .stream().map(
                        PointRecordJpaEntity::toDao
                ).toList();
    }

    @Override
    public PointRecordDao save(PointRecordDao pointRecordDao) {
        return null;
    }

    @Override
    public void deleteById(Long aLong) {

    }
}
