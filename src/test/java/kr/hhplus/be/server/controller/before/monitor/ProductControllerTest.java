package kr.hhplus.be.server.controller.before.monitor;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.TestcontainersConfiguration;
import kr.hhplus.be.server.domain.product.adapter.repository.ProductJpaRepository;
import kr.hhplus.be.server.domain.product.adapter.repository.ProductLineJpaRepository;
import kr.hhplus.be.server.domain.product.application.Product;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.application.repository.ProductLineRepository;
import kr.hhplus.be.server.domain.product.application.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        TestcontainersConfiguration.class
})
@Testcontainers
@AutoConfigureMockMvc(print = MockMvcPrint.SYSTEM_OUT)
@SpringBootTest
public class ProductControllerTest {
//             Body = {"code":"SUCCESS","message":"OK","data":{"products":[{"productId":5,"name":"상품1","price":100},{"productId":6,"name":"상품2","price":200}]}}
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductLineRepository productLineRepository;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private ProductLineJpaRepository productLineJpaRepository;

    @Nested
    @DisplayName("상품 컨트롤러 테스트")
    class ProductEndPointTest{

        @BeforeEach
        void setUp() {
            productJpaRepository.deleteAll();
            productLineJpaRepository.deleteAll();

            ProductLine line1 = ProductLine.builder()
                    .productLineName("라인A")
                    .productId(1L)
                    .productLinePrice(new BigDecimal("1100"))
                    .productLineType("STD")
                    .remaining(10L)
                    .updateDt(LocalDateTime.now())
                    .build();

            Product product1 = Product.builder()
                    .productName("상품1")
                    .productPrice(new BigDecimal(100))
                    .updateDt(LocalDateTime.now())
                    .productLines(List.of(line1))
                    .build();

            ProductLine line2 = ProductLine.builder()
                    .productLineName("라인B")
                    .productId(2L)
                    .productLinePrice(new BigDecimal("2200"))
                    .productLineType("STD")
                    .remaining(10L)
                    .updateDt(LocalDateTime.now())
                    .build();

            Product product2 = Product.builder()
                    .productName("상품2")
                    .productPrice(new BigDecimal(200))
                    .updateDt(LocalDateTime.now())
                    .productLines(List.of(line1))
                    .build();

            productRepository.save(product1);
            productRepository.save(product2);
            productLineRepository.save(line1);
            productLineRepository.save(line2);
        }

        @Test
        @Order(1)
        @DisplayName("GET /products - 상품 목록 조회")
        void listProducts_success() throws Exception {
            mvc.perform(get("/products")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.products[0].name").value("상품1"))
                    .andExpect(jsonPath("$.data.products[1].name").value("상품2"));
        }

        @Test
        @Order(2)
        @DisplayName("GET /products/{productId} - 상품 상세 조회")
        void getProduct_success() throws Exception {
            Long productId = productRepository.findAll().get(0).getProductId();

            mvc.perform(get("/products/{productId}", productId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.productId").value(productId))
                    .andExpect(jsonPath("$.data.name").value("상품1"));
        }

        @Test
        @Order(3)
        @DisplayName("GET /products/top - 인기 상품 조회")
        void topProducts_success() throws Exception {
            mvc.perform(get("/products/top")
                            .param("limit", "5")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("SUCCESS"));
                    //.andExpect(jsonPath("$.data.topProducts[0].productLineName").value("상품1"));
        }

    }
}
