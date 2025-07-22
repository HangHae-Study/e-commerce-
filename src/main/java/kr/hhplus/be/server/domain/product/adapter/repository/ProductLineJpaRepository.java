package kr.hhplus.be.server.domain.product.adapter.repository;

import kr.hhplus.be.server.domain.product.adapter.entity.ProductLineJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductLineJpaRepository extends JpaRepository<ProductLineJpaEntity, Long> {
}
