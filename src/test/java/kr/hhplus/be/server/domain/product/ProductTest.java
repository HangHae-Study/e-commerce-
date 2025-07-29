package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.adapter.entity.ProductJpaEntity;
import kr.hhplus.be.server.domain.product.adapter.repository.ProductJpaRepository;
import kr.hhplus.be.server.domain.product.adapter.repository.ProductJpaRepositoryAdapter;
import kr.hhplus.be.server.domain.product.adapter.repository.ProductLineJpaRepositoryAdapter;
import kr.hhplus.be.server.domain.product.application.Product;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.application.repository.ProductLineRepository;
import kr.hhplus.be.server.domain.product.application.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProductTest {

    @Nested @DisplayName("상품 도메인 단위 테스트")
    class ProductDomainUnitTest{

        @Test
        void 상품_재고_수량_차감_성공(){
            ProductLine item1 = ProductLine.builder()
                    .remaining(10L)
                    .build();

            Long beforeRem = item1.getRemaining();
            Long decrAmount = 1L;

            item1.decreaseStock(decrAmount);

            assertThat(item1.getRemaining()).isEqualTo(beforeRem - decrAmount);
        }

        @ParameterizedTest
        @ValueSource(longs = {11L, 100L, 0, -10})
        void 상품_재고_수량_차감_실패(Long invalidAmount){
            ProductLine item1 = ProductLine.builder()
                    .remaining(10L)
                    .build();

            assertThrows(IllegalArgumentException.class, () -> item1.decreaseStock(invalidAmount));
        }

        @ParameterizedTest
        @ValueSource(longs = {0, -10})
        void 상품_재고_수량_증분_실패(Long invalidAmount){
            ProductLine item1 = ProductLine.builder()
                    .remaining(0L)
                    .build();

            assertThrows(IllegalArgumentException.class, () -> item1.increaseStock(invalidAmount));
        }
    }

    @SpringBootTest
    @Nested @DisplayName("상품 Repository 단위 테스트")
    class ProductRepositoryTest{
        @Autowired
        ProductRepository productRepository;
        @Autowired
        ProductLineRepository productLineRepository;

        @Test
        void 상품_정보_저장_및_조회() {
            // given
            Product p = ProductTestInstance.simpleProduct();

            // when
            Product saved = productRepository.save(p);
            Optional<Product> fetched = productRepository.findById(saved.getProductId());

            // then
            assertThat(fetched).isPresent();
            assertThat(fetched.get().getProductName()).isEqualTo(p.getProductName());
        }

        @Test
        void 상품_및_상품라인_연관_저장_및_조회() {
            // given: Product + Line
            Product p = ProductTestInstance.productWithOneLine();
            ProductLine pl = p.getProductLines().get(0);

            // when
            Product saved = productRepository.save(p);
            pl.setProductId(saved.getProductId());
            ProductLine savedLine = productLineRepository.save(pl);

            List<ProductLine> lines = productLineRepository.findByProductId(saved.getProductId());

            //then
            assertThat(lines)
                    .hasSize(1)
                    .extracting(ProductLine::getProductLineName)
                    .containsExactly(savedLine.getProductLineName());
        }
    }

    /*
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
            when(productRepository.findAll()).thenReturn(List.of(mockproduct));

            List<Product> result = productService.getAllProducts();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getProductId()).isEqualTo(mockgetProductId());
            assertThat(result.get(0).getProductName()).isEqualTo(mockgetProductName());
        }

        @Test
        void 상품_상세내역_조회(){
            TestInstance.MockProduct mockProduct = getMockProduct();
            Long productId = mockgetProductId();
            List<ProductLine> lineList = List.of(mockproductLine1, mockproductLine2);

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
            Product product = mockproduct;
            List<ProductLine> lineList = List.of(mockproductLine1, mockproductLine2);
            Long productId = getProductId();

            when(productService.getProduct(productId)).thenReturn(mockproduct);
            when(productLineService.getProductLineList(getProductId())).thenReturn(lineList);

            ProductDetailResponse detailInfo = productDetailFacade.getProductDetail(productId);

            // then
            assertThat(detailInfo.name()).isEqualTo(getProductName());
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
            productRepository.save(realproduct);
            productLineRepository.save(realproductLine1);
            productLineRepository.save(realproductLine2);
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

    }*/

}
