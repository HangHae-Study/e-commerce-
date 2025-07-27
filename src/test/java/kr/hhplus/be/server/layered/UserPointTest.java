package kr.hhplus.be.server.layered;


import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.user.controller.dto.BalanceChargeRequest;
import kr.hhplus.be.server.domain.user.application.Point;
import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.facade.UserPointFacade;
import kr.hhplus.be.server.domain.user.adapter.repository.InMemoryPointRepository;
import kr.hhplus.be.server.domain.user.adapter.repository.InMemoryUserRepository;
import kr.hhplus.be.server.domain.user.application.repository.PointRepository;
import kr.hhplus.be.server.domain.user.application.repository.UserRepository;
import kr.hhplus.be.server.domain.user.application.service.PointService;
import kr.hhplus.be.server.domain.user.application.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


public class UserPointTest
{
    private UserService userService;
    private UserRepository userRepository;
    @BeforeEach
    void setup(){
        userRepository = new InMemoryUserRepository();
        userService = new UserService(userRepository);
    }

    @Nested @DisplayName("유저 Valid 테스트")
    class UserValidTest{
        @Test
        void 존재하지_않는_유저_조회(){
            Long userId = 10L;
            assertThrows(NoSuchElementException.class, () -> userService.getUser(userId));
        }
    }

    Point getUserPoint() {
        long pointId = ThreadLocalRandom.current().nextLong(1, 10_000);
        long userId = ThreadLocalRandom.current().nextLong(1, 10_000);
        int balance = ThreadLocalRandom.current().nextInt(0, 1_000_001); // 0 ~ 1,000,000

        return new Point(userId, balance);
    }

    @Nested @DisplayName("유저 잔액 도메인 단위 테스트")
    class PointDomainUnitTest{
        @Test
        void 유저_정상_충전(){
            Point point = getUserPoint();
            BigDecimal initBalance = point.getBalance();
            point.charge(200);

            Assertions.assertEquals(initBalance.add(BigDecimal.valueOf(200)), point.getBalance());
        }


        @ParameterizedTest
        @ValueSource(ints = {0, -1, -500})
        void 충전_금액이_0_이하일_경우_예외(int invalidAmount) {
            Point point = getUserPoint();

            assertThrows(IllegalArgumentException.class, () -> point.charge(invalidAmount));
        }

    }


    @Nested
    @DisplayName("잔액 충전 유즈 케이스")
    class UserPointFacadeTest {

        private UserPointFacade userPointFacade;

        private UserRepository userRepository;
        private PointRepository pointRepository;

        @BeforeEach
        void setup() {
            userRepository = new InMemoryUserRepository();
            pointRepository = new InMemoryPointRepository();

            UserService userService = new UserService(userRepository);
            PointService pointService = new PointService(pointRepository);

            userPointFacade = new UserPointFacade(userService, pointService);

            // 초기 유저 등록 (ID = 1)
            Users user = new Users(1L);
            ((InMemoryUserRepository) userRepository).save(user);

            // 초기 포인트 등록 (userId = 1, balance = 0)
            Point point = new Point(1L, 0);
            ((InMemoryPointRepository) pointRepository).save(point);
        }

        @Test
        @DisplayName("유저가 정상적으로 포인트를 충전할 수 있다")
        void 유저_잔액_충전_성공() {
            // when
            Point charged = userPointFacade.chargeUserPoint(1L, 500);

            // then
            assertEquals(new BigDecimal("500"), charged.getBalance());
        }

        @Test
        @DisplayName("존재하지 않는 유저일 경우 예외가 발생한다")
        void 유저가_없을때_예외_발생() {
            assertThrows(NoSuchElementException.class, () -> {
                userPointFacade.chargeUserPoint(999L, 100);
            });
        }

        @Test
        @DisplayName("충전 금액이 0 또는 음수일 경우 예외 발생")
        void 음수_또는_0_금액_충전_예외() {
            assertAll(
                    () -> assertThrows(IllegalArgumentException.class, () -> userPointFacade.chargeUserPoint(1L, 0)),
                    () -> assertThrows(IllegalArgumentException.class, () -> userPointFacade.chargeUserPoint(1L, -100))
            );
        }
    }

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    class ControllerTest{
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        // 필요하다면 테스트용 인메모리 저장소 주입
        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PointRepository pointRepository;

        @BeforeEach
        void setup() {
            userRepository.save(new Users(1L));
            pointRepository.save(new Point(1L, 1L, 1000));
        }

        @Test
        void 유저_잔액_조회_성공() throws Exception {
            mockMvc.perform(get("/points/{userId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.userId").value(1L))
                    .andExpect(jsonPath("$.data.balance").value("1000"));
        }

        @Test
        void 유저_잔액_충전_성공() throws Exception {
            BalanceChargeRequest request = new BalanceChargeRequest(new BigDecimal(500));

            mockMvc.perform(patch("/points/{userId}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.userId").value(1L))
                    .andExpect(jsonPath("$.data.balance").value("1500")); // 기존 1000 + 500
        }
    }
}
