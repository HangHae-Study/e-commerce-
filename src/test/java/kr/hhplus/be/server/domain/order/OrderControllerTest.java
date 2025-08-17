package kr.hhplus.be.server.domain.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.order.controller.dto.OrderCreateRequest;
import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static java.time.LocalDateTime.now;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

@DisplayName("주문 컨트롤러 테스트")
@AutoConfigureMockMvc
@SpringBootTest
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup(){

        userRepository.save(
                Users.builder()
                        .username("테스트")
                        .balance(BigDecimal.ZERO)
                        .createDt(now())
                        .updateDt(now())
                        .build());
    }

    @Test
    @DisplayName("GET /orders/key → 200")
    void 랜덤_주문키_반환() throws Exception {
        mockMvc.perform(get("/orders/key")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.orderCode").isNotEmpty());
    }

    @Test
    @DisplayName("POST /orders → 200, 주문 생성 성공")
    void createOrderSuccess() throws Exception {
        // given
        // (1) 요청 페이로드 준비
        List<OrderCreateRequest.OrderItem> items = List.of(
                new OrderCreateRequest.OrderItem(1L, BigDecimal.valueOf(100), 2),
                new OrderCreateRequest.OrderItem(2L, BigDecimal.valueOf(200), 1)
        );
        OrderCreateRequest req = new OrderCreateRequest(
                null,              // orderId (생성 시엔 null)
                "ORD-12345",       // orderCode
                1L,               // userId
                BigDecimal.valueOf(400), // totalPrice
                items,
                ""                 // couponCode (미사용)
        );

        // when / then
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.orderCode").value("ORD-12345"));
    }
}

