package kr.hhplus.be.server.domain.product.application.facade;

import kr.hhplus.be.server.domain.order.application.service.OrderService;
import kr.hhplus.be.server.domain.order.command.TopOrderProductCommand;
import kr.hhplus.be.server.domain.product.adapter.cache.TopProductCacheRepository;
import kr.hhplus.be.server.domain.product.application.Product;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.application.service.ProductLineService;
import kr.hhplus.be.server.domain.product.application.service.ProductService;
import kr.hhplus.be.server.domain.product.controller.dto.ProductDetailResponse;
import kr.hhplus.be.server.domain.product.controller.dto.ProductListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;
    private final ProductLineService productLineService;

    private final OrderService orderService;
    private final TopProductCacheRepository productCacheRepo;

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

    public List<ProductLine> getTopProductItems(LocalDate start, LocalDate end){
        //LocalDate today = LocalDate.now();
        //LocalDate start = today.minusDays(3); // 오늘부터 -3일전
        //LocalDate end = today.minusDays(1); // 어제

        try{
            List<ProductLine> cached = productCacheRepo.find(start, end);
            if (cached != null) {
                return cached;
            }else{
                throw new NoSuchElementException("주문 상위 상품 Cache Miss.");
            }
        }catch (Exception e){
            //throw e;

            List<TopOrderProductCommand.TopOrderProductResponse> topPlList = orderService.getTopOrderProduct(start, end);

            List<ProductLine> topProductLines = topPlList.stream().map(
                    v -> {
                        return productLineService.getProductLine(v.getProductLineId());
                    }
            ).toList();

            productCacheRepo.save(start, end, topProductLines, ttlUntilMidnight());

            return topProductLines;
        }
    }

    private Duration ttlUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
        return Duration.between(now, midnight);
    }

}
