package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.application.Product;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.application.facade.ProductFacade;
import kr.hhplus.be.server.domain.product.application.repository.ProductLineRepository;
import kr.hhplus.be.server.domain.product.application.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.application.service.ProductLineService;
import kr.hhplus.be.server.domain.product.application.service.ProductService;
import kr.hhplus.be.server.domain.product.controller.dto.ProductDetailResponse;
import kr.hhplus.be.server.domain.product.controller.dto.ProductListResponse;
import kr.hhplus.be.server.domain.product.testinstance.ProductTestInstance;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
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


    @ExtendWith(MockitoExtension.class)
    @DisplayName("상품 Service 테스트")
    @Nested
    class ProductServiceTest{
        @Mock
        ProductRepository productRepository;


        @InjectMocks
        ProductService productService;


        @Test
        void 전체_상품_조회(){
            // given
            List<Product> mockProducts = ProductTestInstance.productList();
            given(productRepository.findAll()).willReturn(mockProducts);

            // when
            List<Product> products = productService.getAllProducts();

            // then
            assertThat(products).hasSize(mockProducts.size())
                    .extracting(Product::getProductName, Product::getProductPrice)
                    .containsExactly(
                            tuple("테스트상품", new BigDecimal("1000")),
                            tuple("테스트상품2", new BigDecimal("2000"))
                    );
        }

        @Test
        void 상품_단일_정보_조회() {
            // given
            Product sample = ProductTestInstance.simpleProduct();
            given(productRepository.findById(42L)).willReturn(Optional.of(sample));

            // when
            Product result = productService.getProduct(42L);

            // then
            assertThat(result).isSameAs(sample);
        }

        @Test
        void 상품_정보_미존재() {
            // given
            given(productRepository.findById(anyLong())).willReturn(Optional.empty());

            // when / then
            assertThrows(NoSuchElementException.class,
                    () -> productService.getProduct(123L),
                    "존재하지 않는 상품 입니다.");
        }
    }

    @ExtendWith(MockitoExtension.class)
    @DisplayName("상품 라인 Service 테스트")
    @Nested
    class ProductLineServiceTest{

        @Mock
        ProductLineRepository productLineRepository;
        @InjectMocks
        ProductLineService productLineService;

        @Test
        void 상품_ID기반_라인_내역_조회(){
            List<ProductLine> mockLines = ProductTestInstance.productLineList();
            given(productLineRepository.findByProductId(anyLong())).willReturn(mockLines);

            List<ProductLine> lines = productLineService.getProductLineList(anyLong());

            assertThat(lines).hasSize(mockLines.size())
                    .extracting(ProductLine::getProductLineName, ProductLine::getProductLineType)
                    .containsExactly(
                            tuple("라인A", "STD")
                    );
        }

        @Test
        void 상품_라인_ID기반_단일_조회(){
            // given
            ProductLine line = ProductTestInstance.simpleLine();
            given(productLineRepository.findById(anyLong())).willReturn(java.util.Optional.of(line));

            // when
            ProductLine result = productLineService.getProductLine(anyLong());

            // then
            assertThat(result).isSameAs(line);
        }

        @Test
        void 상품_라인_미존재(){
            // given
            given(productLineRepository.findById(anyLong())).willReturn(java.util.Optional.empty());

            // when / then
            assertThrows(NoSuchElementException.class,
                    () -> productLineService.getProductLine(99L),
                    "올바르지 않은 상품입니다");
        }
    }

    @ExtendWith(MockitoExtension.class)
    @Nested @DisplayName("상품 도메인 Facade 테스트")
    class ProductFacadeTest {

        @Mock
        private ProductService productService;

        @Mock
        private ProductLineService productLineService;

        @InjectMocks
        private ProductFacade facade;

        @Test
        void 상품_상품라인_전체_조회() {
            // given
            Product p1 = ProductTestInstance.persistedProduct(1L);
            Product p2 = ProductTestInstance.persistedProduct(2L);
            when(productService.getAllProducts()).thenReturn(List.of(p1, p2));

            // when
            ProductListResponse resp = facade.getAllProducts();

            // then
            assertThat(resp.products()).hasSize(2);
            assertThat(resp.products())
                    .extracting("productId", "name", "price")
                    .containsExactly(
                            tuple(1L, p1.getProductName(), p1.getProductPrice()),
                            tuple(2L, p2.getProductName(), p2.getProductPrice())
                    );
        }

        @Test
        void getProductDetail_returnsDetail() {
            // given
            Product p = ProductTestInstance.persistedProduct(1L);
            ProductLine l1 = ProductTestInstance.persistedLine(10L, 1L);
            ProductLine l2 = ProductTestInstance.persistedLine(11L, 1L);
            when(productService.getProduct(1L)).thenReturn(p);
            when(productLineService.getProductLineList(1L)).thenReturn(List.of(l1, l2));

            // when
            ProductDetailResponse detail = facade.getProductDetail(1L);

            // then
            assertThat(detail.productId()).isEqualTo(1L);
            assertThat(detail.name()).isEqualTo(p.getProductName());
            assertThat(detail.lines()).hasSize(2);
            assertThat(detail.lines())
                    .extracting("productLineId", "lineType", "linePrice", "remaining")
                    .containsExactly(
                            tuple(10L, l1.getProductLineType(), l1.getProductLinePrice(), l1.getRemaining()),
                            tuple(11L, l2.getProductLineType(), l2.getProductLinePrice(), l2.getRemaining())
                    );
        }

        @Test
        void getProductDetail_notFound_throws() {
            // given
            when(productService.getProduct(99L))
                    .thenThrow(new NoSuchElementException("존재하지 않는 상품 입니다."));

            // then
            assertThatThrownBy(() -> facade.getProductDetail(99L))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("존재하지 않는 상품 입니다.");
        }
    }

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    class ProductControllerTest{


        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private ProductFacade productFacade;

        @Test
        @DisplayName("GET /products => 200")
        void 상품_목록_조회_요청() throws Exception {
            // given
            var summary1 = new ProductListResponse.ProductSummary(1L, "상품A", new BigDecimal("1000"));
            var summary2 = new ProductListResponse.ProductSummary(2L, "상품B", new BigDecimal("2000"));
            var listResp = new ProductListResponse(List.of(summary1, summary2));
            BDDMockito.given(productFacade.getAllProducts()).willReturn(listResp);

            // when & then
            mockMvc.perform(get("/products")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    // ApiResponse.success 의 데이터 필드 경로가 "data" 라고 가정
                    .andExpect(jsonPath("$.data.products", hasSize(2)))
                    .andExpect(jsonPath("$.data.products[0].productId").value(1))
                    .andExpect(jsonPath("$.data.products[0].name").value("상품A"))
                    .andExpect(jsonPath("$.data.products[0].price").value(1000))
                    .andExpect(jsonPath("$.data.products[1].productId").value(2))
                    .andExpect(jsonPath("$.data.products[1].name").value("상품B"))
                    .andExpect(jsonPath("$.data.products[1].price").value(2000));
        }

        @Test
        @DisplayName("GET /products/{id} => 200")
        void 상품_ID기반_상세정보_조회_요청() throws Exception {
            // given
            var line1 = new ProductDetailResponse.ProductLineItem(10L, "STD", new BigDecimal("1100"), 5L);
            var line2 = new ProductDetailResponse.ProductLineItem(11L, "VIP", new BigDecimal("1500"), 2L);
            var detailResp = new ProductDetailResponse(
                    1L,
                    "상품A",
                    "상세설명",
                    new BigDecimal("1000"),
                    List.of(line1, line2)
            );
            BDDMockito.given(productFacade.getProductDetail(1L)).willReturn(detailResp);

            // when & then
            mockMvc.perform(get("/products/1")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data.productId").value(1))
                    .andExpect(jsonPath("$.data.name").value("상품A"))
                    .andExpect(jsonPath("$.data.description").value("상세설명"))
                    .andExpect(jsonPath("$.data.price").value(1000))
                    .andExpect(jsonPath("$.data.lines", hasSize(2)))
                    .andExpect(jsonPath("$.data.lines[0].productLineId").value(10))
                    .andExpect(jsonPath("$.data.lines[0].lineType").value("STD"))
                    .andExpect(jsonPath("$.data.lines[0].linePrice").value(1100))
                    .andExpect(jsonPath("$.data.lines[0].remaining").value(5))
                    .andExpect(jsonPath("$.data.lines[1].productLineId").value(11))
                    .andExpect(jsonPath("$.data.lines[1].lineType").value("VIP"))
                    .andExpect(jsonPath("$.data.lines[1].linePrice").value(1500))
                    .andExpect(jsonPath("$.data.lines[1].remaining").value(2));
        }

        @Test
        @DisplayName("GET /products/{id} => 존재하지 않는 ID 조회시 404")
        void 상품I미존재_ID기반_요청() throws Exception {
            // given
            BDDMockito.given(productFacade.getProductDetail(99L))
                    .willThrow(new NoSuchElementException("존재하지 않는 상품 입니다."));

            // when & then
            mockMvc.perform(get("/products/99")
                            .accept(MediaType.APPLICATION_JSON))
                    // 컨트롤러에 전용 ExceptionHandler 가 없으면 500이지만,
                    // NoSuchElementException을 404로 매핑하는 @ControllerAdvice가 있다면 isNotFound()
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("존재하지 않는 상품 입니다."));
        }

    }

}
