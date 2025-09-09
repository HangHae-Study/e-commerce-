package kr.hhplus.be.server.controller.before.monitor;

import kr.hhplus.be.server.TestcontainersConfiguration;
import kr.hhplus.be.server.domain.order.adapter.repository.OrderJpaRepository;
import kr.hhplus.be.server.domain.product.adapter.repository.ProductJpaRepository;
import kr.hhplus.be.server.domain.product.adapter.repository.ProductLineJpaRepository;
import kr.hhplus.be.server.domain.product.application.Product;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.application.repository.ProductLineRepository;
import kr.hhplus.be.server.domain.product.application.repository.ProductRepository;
import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.dto.PointDao;
import kr.hhplus.be.server.domain.user.application.repository.PointRepository;
import kr.hhplus.be.server.domain.user.application.repository.UserRepository;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        TestcontainersConfiguration.class
})
@Testcontainers
@AutoConfigureMockMvc(print = MockMvcPrint.SYSTEM_OUT)
@SpringBootTest
public class OrderControllerTest {

    @Autowired
    private MockMvc mvc;

    //@Autowired
    //private ObjectMapper om;


    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private ProductLineJpaRepository productLineJpaRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductLineRepository productLineRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    PointRepository pointRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        pointRepository.deleteAll();
        orderJpaRepository.deleteAll();
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

        // 테스트 데이터 세팅
        Users user = Users.builder()
                .userId(null)
                .username("test1")
                .balance(new BigDecimal("10000"))
                .createDt(LocalDateTime.now())
                .updateDt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        PointDao point = PointDao.builder()
                .pointId(null)
                .userId(1L)
                .balance(new BigDecimal(10000))
                .updateDt(LocalDateTime.now())
                .pointRecords(new ArrayList<>())
                .build();
        pointRepository.save(point);
    }

    @Nested
    @DisplayName("주문 키 발급")
    class IssueOrderKey {

        @Test
        @DisplayName("GET /orders/key - 성공")
        void issueOrderKey_success() throws Exception {
            mvc.perform(get("/orders/key")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.orderCode").exists());
        }
    }

    @Nested
    @DisplayName("주문 생성")
    class CreateOrder {

        @Test
        @DisplayName("POST /orders - 성공")
        void createOrder_success() throws Exception {
            Long productId = productJpaRepository.findAll().get(0).getProductId();

            String orderCode = UUID.randomUUID().toString();

            // OrderCreateRequest JSON (예시)
            String body = """
                {
                  "orderCode": "%s",
                  "userId": 1,
                  "totalPrice": 400,
                  "couponCode": "",
                  "items": [
                    {
                      "productLineId": %d,
                      "linePrice" : 100,
                      "quantity": 2
                    }
                  ]
                }
                """.formatted(orderCode, productId);

            mvc.perform(post("/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.orderCode").value(orderCode))
                    .andExpect(jsonPath("$.data.items[0].productLineId").value(productId))
                    .andExpect(jsonPath("$.data.items[0].quantity").value(2))
                    .andExpect(jsonPath("$.data.totalPrice").value(400)); // 1000 * 2
        }
    }
}
