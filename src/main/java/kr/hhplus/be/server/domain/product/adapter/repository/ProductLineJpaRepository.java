package kr.hhplus.be.server.domain.product.adapter.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.product.adapter.entity.ProductLineJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductLineJpaRepository extends JpaRepository<ProductLineJpaEntity, Long> {
    List<ProductLineJpaEntity> findProductLineJpaEntitiesByProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ProductLineJpaEntity p where p.productLineId = :id")
    Optional<ProductLineJpaEntity> findByIdForUpdate(@Param("id") Long plId);
}
