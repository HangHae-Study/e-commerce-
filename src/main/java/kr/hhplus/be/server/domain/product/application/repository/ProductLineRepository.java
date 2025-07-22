package kr.hhplus.be.server.domain.product.application.repository;

import kr.hhplus.be.server.common.RepositoryPort;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductLineRepository extends RepositoryPort<Long, ProductLine> {

    List<ProductLine> findByProductId(Long productId);

}
