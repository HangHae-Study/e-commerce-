package kr.hhplus.be.server.domain.product.adapter.repository;

import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.application.repository.ProductLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductLineJpaRepositoryAdapter implements ProductLineRepository {
    @Override
    public Optional<ProductLine> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<ProductLine> findAll() {
        return null;
    }

    @Override
    public ProductLine save(ProductLine productLine) {
        return null;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public List<ProductLine> findByProductId(Long productId) {
        return null;
    }
}
