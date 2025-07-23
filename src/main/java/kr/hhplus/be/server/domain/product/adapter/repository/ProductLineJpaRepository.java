package kr.hhplus.be.server.domain.product.adapter.repository;

import kr.hhplus.be.server.domain.product.adapter.entity.ProductLineJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductLineJpaRepository extends JpaRepository<ProductLineJpaEntity, Long> {
}
