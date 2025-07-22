package kr.hhplus.be.server.domain.product.adapter.repository;

import kr.hhplus.be.server.domain.product.application.Product;
import kr.hhplus.be.server.domain.product.application.repository.ProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, Long> {
}
