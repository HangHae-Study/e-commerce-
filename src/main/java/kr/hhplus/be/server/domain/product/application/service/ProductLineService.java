package kr.hhplus.be.server.domain.product.application.service;

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
        // Step 09 : 비관적 락으로 수정
        return productLineRepository.findByIdWithPessimisticLock(productId)
                //productLineRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("올바르지 않은 상품입니다"));
    }

    public void updateProductLine(ProductLine product){
        productLineRepository.save(product);
    }

}
