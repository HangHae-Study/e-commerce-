package kr.hhplus.be.server.domain.product.controller;

import kr.hhplus.be.server.common.api.ApiResponse;
import kr.hhplus.be.server.domain.product.application.facade.ProductDetailFacade;
import kr.hhplus.be.server.domain.product.controller.apidto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductDetailFacade productDetailFacade;

    @GetMapping
    public ResponseEntity<ApiResponse<ProductListResponse>> listProducts() {
        List<ProductSummary> products = List.of(
                new ProductSummary(1L, "사과", 1000),
                new ProductSummary(2L, "바나나", 2000)
        );
        return ResponseEntity.ok(ApiResponse.success(new ProductListResponse(products)));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(productDetailFacade.getProductDetail(productId)));
    }

    @GetMapping("/top")
    public ResponseEntity<ApiResponse<TopProductsResponse>> topProducts(
            @RequestParam(name = "limit", defaultValue = "5") int limit) {
        List<TopProductRanking> rankings = List.of(
                new TopProductRanking(1L, "인기 1순위 상품", 100.0, 100),
                new TopProductRanking(2L, "인기 2순위 상품", 80.0, 80),
                new TopProductRanking(3L, "인기 3순위 상품", 60.0, 60),
                new TopProductRanking(4L, "인기 4순위 상품", 40.0, 40),
                new TopProductRanking(5L, "인기 5순위 상품", 20.0, 20)
        );
        List<TopProductRanking> topN = rankings.stream().limit(limit).toList();
        return ResponseEntity.ok(ApiResponse.success(new TopProductsResponse(topN)));
    }
}