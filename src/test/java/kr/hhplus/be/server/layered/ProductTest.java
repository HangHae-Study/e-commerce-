package kr.hhplus.be.server.layered;

import kr.hhplus.be.server.domain.product.application.Product;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.application.facade.ProductDetailFacade;
import kr.hhplus.be.server.domain.product.application.repository.ProductLineRepository;
import kr.hhplus.be.server.domain.product.application.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.application.service.ProductLineService;
import kr.hhplus.be.server.domain.product.application.service.ProductService;
import kr.hhplus.be.server.domain.product.controller.apidto.ProductDetailResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class ProductTest {

    class MockProduct{
        final Product product = Product.builder()
                .productId(1L)
                .productName("맥북 14 Pro")
                .productPrice(new BigDecimal("32000"))
                .updateDt(LocalDateTime.now())
                .build();

        final ProductLine productLine1 = ProductLine.builder()
                .productLineId(1L)
                .productId(1L)
                .productName("맥북 14 Pro")
                .productLinePrice(new BigDecimal("31000"))
                .productLineType("m2 그레이 실버")
                .updateDt(LocalDateTime.now())
                .build();

        final ProductLine productLine2 = ProductLine.builder()
                .productLineId(1L)
                .productId(1L)
                .productName("맥북 14 Pro")
                .productLinePrice(new BigDecimal("33000"))
                .productLineType("m3 스페이스 블랙")
                .updateDt(LocalDateTime.now())
                .build();
    }


    @ExtendWith(MockitoExtension.class)
    @DisplayName("상품 조회 Mock 테스트")
    @Nested
    class ProductMockTest{
        @Mock
        ProductRepository productRepository;

        @Mock
        ProductLineRepository productLineRepository;

        @InjectMocks
        ProductService productService;

        @InjectMocks
        ProductLineService productLineService;

        @Test
        void 전체_상품_조회(){
            MockProduct mockProduct = new MockProduct();
            when(productRepository.findAll()).thenReturn(List.of(mockProduct.product));

            List<Product> result = productService.getAllProducts();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getProductId()).isEqualTo(mockProduct.product.getProductId());
            assertThat(result.get(0).getProductName()).isEqualTo(mockProduct.product.getProductName());
        }

        @Test
        void 상품_상세내역_조회(){
            MockProduct mockProduct = new MockProduct();
            Long productId = mockProduct.product.getProductId();
            List<ProductLine> lineList = List.of(mockProduct.productLine1, mockProduct.productLine2);

            when(productLineRepository.findByProductId(productId)).thenReturn(lineList);

            List<ProductLine> result = productLineService.getProductLineList(productId);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getProductId()).isEqualTo(productId);
            assertThat(result.get(0).getProductLineId()).isEqualTo(lineList.get(0).getProductLineId());
            assertThat(result.get(0).getProductLineType()).isEqualTo(lineList.get(0).getProductLineType());
        }
    }

    @InjectMocks
    ProductDetailFacade productDetailFacade;

    @ExtendWith(MockitoExtension.class)
    @Nested @DisplayName("Product Detail Facade 테스트")
    class ProductUseCaseTest{
        @Mock
        ProductService productService;

        @Mock
        ProductLineService productLineService;

        ProductDetailFacade productDetailFacade;

        @BeforeEach
        void setUp() {
            productDetailFacade = new ProductDetailFacade(productService, productLineService);
        }
        @Test
        void 상품_상품라인_조합_조회_테스트(){
            MockProduct mockProduct = new MockProduct();
            Product product = mockProduct.product;
            List<ProductLine> lineList = List.of(mockProduct.productLine1, mockProduct.productLine2);
            Long productId = product.getProductId();

            when(productService.getProduct(productId)).thenReturn(mockProduct.product);
            when(productLineService.getProductLineList(product.getProductId())).thenReturn(lineList);

            ProductDetailResponse detailInfo = productDetailFacade.getProductDetail(productId);

            // then
            assertThat(detailInfo.name()).isEqualTo(product.getProductName());
            assertThat(detailInfo.lines()).hasSize(lineList.size());
            assertThat(detailInfo.lines()).extracting("lineType")
                    .containsExactlyInAnyOrder(
                            lineList.get(0).getProductLineType(),
                            lineList.get(1).getProductLineType());
        }

        @Test
        @DisplayName("상품 ID가 존재하지 않으면 예외 발생")
        void getProductDetail_productNotFound() {
            // given
            Long productId = 99L;

            when(productService.getProduct(productId))
                    .thenThrow(new NoSuchElementException("해당 상품이 존재하지 않습니다."));

            // when & then
            assertThrows(NoSuchElementException.class,
                    () -> productDetailFacade.getProductDetail(productId));
        }
    }

}
