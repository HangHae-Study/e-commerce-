package kr.hhplus.be.server.domain.product.application.service;

import kr.hhplus.be.server.domain.product.application.Product;
import kr.hhplus.be.server.domain.product.application.repository.ProductLineRepository;
import kr.hhplus.be.server.domain.product.application.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Product getProduct(Long productId){
        return productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 상품 입니다."));
    }

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }
}
