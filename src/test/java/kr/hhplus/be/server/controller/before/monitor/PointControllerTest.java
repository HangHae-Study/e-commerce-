package kr.hhplus.be.server.controller.before.monitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import kr.hhplus.be.server.TestcontainersConfiguration;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        TestcontainersConfiguration.class
})
@Testcontainers
@AutoConfigureMockMvc(print = MockMvcPrint.SYSTEM_OUT)
@Transactional
@SpringBootTest
public class PointControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PointRepository pointRepository;

    @BeforeEach
    void setUp() {

        userRepository.deleteAll();
        pointRepository.deleteAll();

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
    @DisplayName("유저 컨트롤러 테스트")
    class UserPointEndPointTest{


        @Test
        @DisplayName("GET /points/{userId} - 잔액 조회 성공")
        void getBalance_success() throws Exception {
            mvc.perform(get("/points/{userId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.userId").value(1L))
                    .andExpect(jsonPath("$.data.balance").value(10_000L));
        }

        @Test
        @DisplayName("GET /points/{userId}/reqId - 요청 아이디 생성 성공(형식 검증)")
        void getReqId_success() throws Exception {
            mvc.perform(get("/points/{userId}/reqId", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.matchesPattern("^1_.+_CHARGE$")));
        }

        @Test
        @DisplayName("PATCH /points/{userId} - 충전 성공")
        void charge_success() throws Exception {
            String body = """
          { "amount": 5000, "reqId": "1_2025-09-09T09:00:00_CHARGE" }
        """;

            mvc.perform(patch("/points/{userId}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.userId").value(1L))
                    .andExpect(jsonPath("$.data.balance").value(15_000L));
        }
    }
}
