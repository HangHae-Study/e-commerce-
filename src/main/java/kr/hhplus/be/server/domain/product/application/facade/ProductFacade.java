package kr.hhplus.be.server.domain.product.application.facade;

import kr.hhplus.be.server.domain.product.application.Product;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.application.service.ProductLineService;
import kr.hhplus.be.server.domain.product.application.service.ProductService;
import kr.hhplus.be.server.domain.product.controller.apidto.ProductDetailResponse;
import kr.hhplus.be.server.domain.product.controller.apidto.ProductListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;
    private final ProductLineService productLineService;

    @Transactional(readOnly = true)
    public ProductListResponse getAllProducts(){
        List<Product> products = productService.getAllProducts();

        List<ProductListResponse.ProductSummary> productRes = products.stream().map(
                product -> {
                    return new ProductListResponse.ProductSummary(
                            product.getProductId(),
                            product.getProductName(),
                            product.getProductPrice());
                }).toList();

        return new ProductListResponse(productRes);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetail(Long productId){
        Product product = productService.getProduct(productId);
        List<ProductLine> productLines = productLineService.getProductLineList(productId);

        List<ProductDetailResponse.ProductLineItem> lines = productLines.stream().map(line ->
                new ProductDetailResponse.ProductLineItem(
                        line.getProductLineId(),
                        line.getProductLineType(),
                        line.getProductLinePrice(),
                        line.getRemaining()
                )).toList();

        return new ProductDetailResponse(
                product.getProductId(),
                product.getProductName(),
                "Product Descript",
                product.getProductPrice(),
                lines
        );
    }

}
