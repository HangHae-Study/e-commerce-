package kr.hhplus.be.server.layered;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.TestData.TestInstance;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static kr.hhplus.be.server.TestData.TestInstance.MockProduct.getMockProduct;
import static kr.hhplus.be.server.TestData.TestInstance.PersistProduct.getPersistProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProductTest {

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
            TestInstance.MockProduct mockProduct = getMockProduct();
            when(productRepository.findAll()).thenReturn(List.of(mockProduct.product));

            List<Product> result = productService.getAllProducts();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getProductId()).isEqualTo(mockProduct.product.getProductId());
            assertThat(result.get(0).getProductName()).isEqualTo(mockProduct.product.getProductName());
        }

        @Test
        void 상품_상세내역_조회(){
            TestInstance.MockProduct mockProduct = getMockProduct();
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
            TestInstance.MockProduct mockProduct = getMockProduct();
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

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @Testcontainers
    class ProductControllerTest{


        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private ProductLineRepository productLineRepository;

        @BeforeEach
        void setup(){
            TestInstance.PersistProduct realProduct = getPersistProduct();
            productRepository.save(realProduct.product);
            productLineRepository.save(realProduct.productLine1);
            productLineRepository.save(realProduct.productLine2);
        }

        @Test
        void 상품_목록_조회_요청() throws Exception{
            mockMvc.perform(get("/products"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.products.length()").value(1))
                    .andExpect(jsonPath("$.data.products[0].productId").value(1))
                    .andExpect(jsonPath("$.data.products[0].name").value("맥북 14 Pro"));
        }

        @Test
        void 상품_상세_조회_요청() throws Exception{
            mockMvc.perform(get("/products/{productId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.productId").value(1))
                    .andExpect(jsonPath("$.data.name").value("맥북 14 Pro"))
                    .andExpect(jsonPath("$.data.lines.length()").value(2))
                    .andExpect(jsonPath("$.data.lines[0].productLineId").value(1))
                    .andExpect(jsonPath("$.data.lines[0].lineType").value("m2 그레이 실버"));
        }


        @Test
        void 존재하지_않는_상품_조회_요청() throws Exception{Long nonExistentProductId = 9999L;

            // when & then
            mockMvc.perform(get("/products/{productId}", nonExistentProductId))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("NOT_FOUND"))  // ApiResponse 구조에 따라 다름
                    .andExpect(jsonPath("$.message").exists());
        }

    }

}
