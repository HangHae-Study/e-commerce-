package kr.hhplus.be.server.domain.product.application.facade;

import kr.hhplus.be.server.domain.product.application.Product;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.application.service.ProductLineService;
import kr.hhplus.be.server.domain.product.application.service.ProductService;
import kr.hhplus.be.server.domain.product.controller.apidto.ProductDetailResponse;
import kr.hhplus.be.server.domain.product.controller.apidto.ProductLineItem;
import kr.hhplus.be.server.domain.product.controller.apidto.ProductListResponse;
import kr.hhplus.be.server.domain.product.controller.apidto.ProductSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductDetailFacade {

    private final ProductService productService;
    private final ProductLineService productLineService;

    public ProductListResponse getAllProducts(){
        List<Product> products = productService.getAllProducts();

        List<ProductSummary> productRes = products.stream().map(
                product -> {
                    return new ProductSummary(
                            product.getProductId(),
                            product.getProductName(),
                            product.getProductPrice());
                }).toList();

        return new ProductListResponse(productRes);
    }

    public ProductDetailResponse getProductDetail(Long productId){
        Product product = productService.getProduct(productId);
        List<ProductLine> productLines = productLineService.getProductLineList(productId);

        List<ProductLineItem> lines = productLines.stream().map(line ->
                new ProductLineItem (
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
