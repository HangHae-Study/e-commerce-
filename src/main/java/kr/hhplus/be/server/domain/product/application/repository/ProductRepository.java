package kr.hhplus.be.server.domain.product.application.repository;

import kr.hhplus.be.server.common.RepositoryPort;
import kr.hhplus.be.server.domain.product.application.Product;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends RepositoryPort<Long, Product> {
}
