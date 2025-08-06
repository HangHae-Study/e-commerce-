package kr.hhplus.be.server.domain.product.application.service;

import kr.hhplus.be.server.domain.product.application.Product;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.application.repository.ProductLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProductLineService {
    private final ProductLineRepository productLineRepository;

    public List<ProductLine> getProductLineList(Long productId){
        return productLineRepository.findByProductId(productId);
    }

    public ProductLine getProductLine(Long productId){
        return productLineRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("올바르지 않은 상품입니다"));
    }


    @Transactional
    public ProductLine updateProductLine(ProductLine product){
        return productLineRepository.save(product);
    }

}
