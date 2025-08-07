package kr.hhplus.be.server.domain.user.adapter.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.user.adapter.entity.PointJpaEntity;
import kr.hhplus.be.server.domain.user.application.dto.PointDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//@Repository
public interface PointJpaRepository extends JpaRepository<PointJpaEntity, Long> {
    public Optional<PointJpaEntity> findPointJpaRepositoryByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PointJpaEntity p where p.userId = :id ")
    public Optional<PointJpaEntity> findByUserIdForUpdate(@Param("id") Long userId);

}
