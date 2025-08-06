package kr.hhplus.be.server.domain.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.user.adapter.entity.UsersJpaEntity;
import kr.hhplus.be.server.domain.user.adapter.repository.UsersJpaRepository;
import kr.hhplus.be.server.domain.user.application.dto.PointDao;
import kr.hhplus.be.server.domain.user.controller.dto.BalanceChargeRequest;
import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.repository.PointRepository;
import kr.hhplus.be.server.domain.user.application.repository.UserRepository;
import kr.hhplus.be.server.domain.user.application.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


public class UserPointTest
{


    @Nested @DisplayName("유저 잔액 도메인 단위 테스트")
    class PointDomainUnitTest{
        @Test
        void 유저_정상_충전(){
            Users user = Users.builder()
                    .balance(BigDecimal.ZERO)
                    .build();

            BigDecimal initBalance = user.getBalance();
            user.pointCharge(200);

            Assertions.assertEquals(initBalance.add(BigDecimal.valueOf(200)), user.getBalance());
        }


        @ParameterizedTest
        @ValueSource(ints = {0, -1, -500})
        void 충전_금액이_0_이하일_경우_예외(int invalidAmount) {
            Users user = Users.builder()
                    .balance(BigDecimal.ZERO)
                    .build();

            assertThrows(IllegalArgumentException.class, () -> user.pointCharge(invalidAmount));
        }

        @Test
        void 유저_포인트_사용_성공(){
            Users user = Users.builder()
                    .balance(BigDecimal.valueOf(100L))
                    .build();
            BigDecimal initBalance = user.getBalance();

            user.pointUse(50L);

            Assertions.assertEquals(initBalance.subtract(BigDecimal.valueOf(50)), user.getBalance());
        }

        @Test
        void 유저_포인트_사용_금액_초과(){
            Users user = Users.builder()
                    .balance(BigDecimal.valueOf(40))
                    .build();
            BigDecimal initBalance = user.getBalance();

            assertThrows(IllegalArgumentException.class, () -> user.pointUse(50L));
        }
    }

    @SpringBootTest
    @Nested @DisplayName("유저 잔액 Repository 단위 테스트")
    class UserRepositoryTest{
        @Autowired
        UserRepository userRepository;

        @Test
        void 유저_저장_및_조회() {
            Users saved = userRepository.save(
                    Users.builder()
                            .username("testUser")
                            .balance(BigDecimal.valueOf(40))
                            .build()
            );

            Optional<Users> result = userRepository.findById(saved.getUserId());

            assertThat(result).isPresent();
            assertThat(result.get().getUserId()).isNotEqualTo(null);
            assertThat(result.get().getUsername()).isEqualTo("testUser");
        }
    }

    @ExtendWith(MockitoExtension.class)
    @Nested @DisplayName("유저 잔액 Service 단위 테스트")
    @ContextConfiguration(classes = TestcontainersConfiguration.class)
    class UserServiceTest{
        @InjectMocks
        private UserService userService;

        @Mock
        private UserRepository userRepository;
        @Mock
        private PointRepository pointRepository;

        @Test
        void 포인트_충전_성공() {
            // given
            Long userId = 1L;
            BigDecimal 기존잔액 = new BigDecimal("1000");

            Users user = Users.builder()
                    .userId(userId)
                    .username("테스트")
                    .balance(기존잔액)
                    .createDt(now())
                    .updateDt(now())
                    .build();

            PointDao pointDao = PointDao.builder()
                    .userId(userId)
                    .balance(기존잔액)
                    .build();

            when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(pointDao));
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.save(any())).thenReturn(user);

            // when
            Users result = userService.chargePoint(userId, 500);

            // then
            assertThat(result.getBalance()).isEqualByComparingTo("1500");

            verify(userRepository).save(user);
        }

        @Test
        void 유저가_없으면_예외_발생() {
            Long userId = 999L;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.chargePoint(userId, 500))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("존재하지 않는 유저");
        }

        @Test
        void 충전_도중_예외_발생시_DB저장은_되지_않는다() {
            Users user = Users.builder()
                    .userId(1L)
                    .username("테스트")
                    .balance(BigDecimal.ZERO)
                    .createDt(now())
                    .updateDt(now())
                    .build();

            PointDao pointDao = PointDao.builder()
                    .userId(1L)
                    .balance(BigDecimal.ZERO)
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(pointRepository.findByUserId(1L)).thenReturn(Optional.of(pointDao));

            assertThatThrownBy(() -> userService.chargePoint(1L, -999))
                    .isInstanceOf(IllegalArgumentException.class);

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @Testcontainers
    @ContextConfiguration(classes = TestcontainersConfiguration.class)
    class ControllerTest{
        @Autowired
        private MockMvc mockMvc;

        private ObjectMapper objectMapper;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PointRepository pointRepository;

        @BeforeEach
        void setup() {

           objectMapper = new ObjectMapper();
           userRepository.save(
                    Users.builder()
                        .username("테스트")
                        .balance(BigDecimal.ZERO)
                        .createDt(now())
                        .updateDt(now())
                        .build());
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
