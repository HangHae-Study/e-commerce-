package kr.hhplus.be.server.domain.product.application.facade;

import kr.hhplus.be.server.domain.order.application.service.OrderService;
import kr.hhplus.be.server.domain.order.command.TopOrderProductCommand;
import kr.hhplus.be.server.domain.product.adapter.cache.TopProductCacheRepository;
import kr.hhplus.be.server.domain.product.application.Product;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.application.service.ProductLineService;
import kr.hhplus.be.server.domain.product.application.service.ProductService;
import kr.hhplus.be.server.domain.product.command.ProductRankingDto;
import kr.hhplus.be.server.domain.product.controller.dto.ProductDetailResponse;
import kr.hhplus.be.server.domain.product.controller.dto.ProductListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    public List<ProductLine> getTopFiveProductForThreeDays(){
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(3); // 오늘부터 -3일전
        LocalDate end = today.minusDays(1); // 어제
        return getTopProductItems(start, end);
    }

    public List<ProductLine> getTopProductItemsWihtOutCache(LocalDate start, LocalDate end){
        List<TopOrderProductCommand.TopOrderProductResponse> topPlList = orderService.getTopOrderProduct(start, end);

        List<ProductLine> topProductLines = topPlList.stream().map(
                v -> productLineService.getProductLine(v.getProductLineId())
        ).toList();

        return topProductLines;
    }


    public List<ProductLine> getTopProductItems(LocalDate start, LocalDate end){
        try{
            List<ProductLine> cached = productCacheRepo.findTop5RankFor3Days(start, end);
            if (cached != null) {
                return cached;
            }else{
                //캐시 스탬피드 방지 락
                if(productCacheRepo.getLockForTop5RankFor3Days(start, end)){
                    List<ProductRankingDto.ProductRankEntry> ranks = productCacheRepo.topFiveForThreeDaysZset();

                    // rank가 null 이라면, 주문 정보 기준 DB 조회 후 Cache 만들어주기
                    if(ranks == null || ranks.isEmpty()){
                        List<TopOrderProductCommand.TopOrderProductResponse> topPlList = orderService.getTopOrderProduct(start, end);

                        List<ProductLine> topProductLines = topPlList.stream().map(
                                v -> productLineService.getProductLine(v.getProductLineId())
                        ).toList();

                        productCacheRepo.refreshThreeDaysZsetFromDb(start, end, topPlList);
                        productCacheRepo.saveTop5RankFor3Days(start, end, topProductLines);

                        return topProductLines;
                    }
                    // rank가 null이 아니라면, 상품 정보 기준 DB 조회 후 상품 Cache 만들어 주기
                    else{
                        List<ProductLine> topProductLines = ranks.stream().map(
                                rank -> productLineService.getProductLine(rank.productLineId())
                        ).toList();

                        productCacheRepo.saveTop5RankFor3Days(start, end, topProductLines);

                        return topProductLines;
                    }
                }else{
                    throw new NoSuchElementException("주문 상위 상품 Cache Miss.");
                }
            }
        }catch (Exception e){
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
            List<ProductLine> warmed = productCacheRepo.findTop5RankFor3Days(start, end);
            if (warmed != null) return warmed;

            throw e;
        }
    }

}
