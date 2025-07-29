package kr.hhplus.be.server.domain.user.adapter.repository;

import kr.hhplus.be.server.domain.user.adapter.entity.PointJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//@Repository
public interface PointJpaRepository extends JpaRepository<PointJpaEntity, Long> {
    public Optional<PointJpaEntity> findPointJpaRepositoryByUserId(Long userId);
}
