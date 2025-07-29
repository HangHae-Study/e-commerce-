package kr.hhplus.be.server.domain.user.adapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRecordJpaRepository extends JpaRepository<PointRecordJpaRepository, Long> {
}
