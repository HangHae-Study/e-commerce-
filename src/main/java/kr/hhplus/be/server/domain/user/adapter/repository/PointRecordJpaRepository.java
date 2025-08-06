package kr.hhplus.be.server.domain.user.adapter.repository;

import kr.hhplus.be.server.domain.user.adapter.entity.PointRecordJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRecordJpaRepository extends JpaRepository<PointRecordJpaEntity, Long> {
}
