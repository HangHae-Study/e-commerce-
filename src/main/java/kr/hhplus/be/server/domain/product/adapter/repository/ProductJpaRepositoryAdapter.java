package kr.hhplus.be.server.domain.product.adapter.repository;

import kr.hhplus.be.server.domain.product.adapter.entity.ProductJpaEntity;
import kr.hhplus.be.server.domain.product.application.Product;
import kr.hhplus.be.server.domain.product.application.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductJpaRepositoryAdapter implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;

    @Override
    public Optional<Product> findById(Long aLong) {
        return productJpaRepository.findById(aLong).map(ProductJpaEntity::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll().stream()
                .map(ProductJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity p =  productJpaRepository.save(ProductJpaEntity.fromDomain(product));
        return p.toDomain();
    }

    @Override
    public void deleteById(Long aLong) {

    }
}
