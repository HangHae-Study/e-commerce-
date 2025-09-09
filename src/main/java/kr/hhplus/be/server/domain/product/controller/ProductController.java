package kr.hhplus.be.server.domain.product.controller;

import kr.hhplus.be.server.common.api.ApiResponse;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.application.facade.ProductFacade;
import kr.hhplus.be.server.domain.product.controller.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductFacade productFacade;

    @GetMapping
    public ResponseEntity<ApiResponse<ProductListResponse>> listProducts() {
        return ResponseEntity.ok(ApiResponse.success(productFacade.getAllProducts()));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(productFacade.getProductDetail(productId)));
    }

    @GetMapping("/top")
    public ResponseEntity<ApiResponse<TopProductsResponse>> topProducts(
            @RequestParam(name = "limit", defaultValue = "5") int limit) {

        List<ProductLine> topProducts = productFacade.getTopFiveProductForThreeDays();

        List<TopProductRanking> topN = topProducts.stream().map(
                a -> new TopProductRanking(a.getProductLineId(), a.getProductLineName(), a.getProductLinePrice(), a.getRemaining())
        ).toList();

        return ResponseEntity.ok(ApiResponse.success(new TopProductsResponse(topN)));
    }
}