package kr.hhplus.be.server.domain.product.application.service;

import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.application.repository.ProductLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductLineService {
    private final ProductLineRepository productLineRepository;

    public List<ProductLine> getProductLineList(Long productId){
        return productLineRepository.findByProductId(productId);
    }
}
