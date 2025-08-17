package kr.hhplus.be.server.domain.product.adapter.repository;

import kr.hhplus.be.server.domain.product.adapter.entity.ProductLineJpaEntity;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.application.repository.ProductLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductLineJpaRepositoryAdapter implements ProductLineRepository {
    private final ProductLineJpaRepository productLineJpaRepository;

    @Override
    public Optional<ProductLine> findById(Long aLong) {
        return productLineJpaRepository.findById(aLong).map(ProductLineJpaEntity::toDomain);
    }

    @Override
    public Optional<ProductLine> findByIdWithPessimisticLock(Long plId){
        return productLineJpaRepository.findByIdForUpdate(plId).map(ProductLineJpaEntity::toDomain);
    }

    @Override
    public List<ProductLine> findAll() {
        return productLineJpaRepository.findAll()
                .stream().map(
                        ProductLineJpaEntity::toDomain
                ).toList();
    }

    @Override
    public ProductLine save(ProductLine domain) {
        ProductLineJpaEntity e = ProductLineJpaEntity.fromDomain(domain);
        ProductLineJpaEntity saved = productLineJpaRepository.save(e);
        return saved.toDomain();
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public List<ProductLine> findByProductId(Long productId) {
        return productLineJpaRepository.findProductLineJpaEntitiesByProductId(productId)
                .stream().map(ProductLineJpaEntity::toDomain
                ).toList();
    }
}
