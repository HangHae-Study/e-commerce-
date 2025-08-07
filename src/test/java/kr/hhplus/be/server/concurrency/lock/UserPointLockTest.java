package kr.hhplus.be.server.concurrency.lock;

import kr.hhplus.be.server.TestcontainersConfiguration;
import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.dto.PointDao;
import kr.hhplus.be.server.domain.user.application.repository.PointRepository;
import kr.hhplus.be.server.domain.user.application.repository.UserRepository;
import kr.hhplus.be.server.domain.user.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        TestcontainersConfiguration.class,
})
public class UserPointLockTest {
    @Nested
    @SpringBootTest
    class UserServiceConcurrencyTest {

        @Autowired
        private UserService userService;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PointRepository pointRepository;

        @Test
        @DisplayName("Concurrent chargePointWithLock should accumulate balance correctly")
        @Sql("classpath:sql/lock/UserPoint.sql")
        void concurrentChargePointWithLock() {
            int threads = 10;
            BigDecimal amount = BigDecimal.valueOf(100);

            ExecutorService executor = Executors.newFixedThreadPool(10);
            // Perform concurrent charge operations
            List<CompletableFuture<Boolean>> futures = IntStream.range(0, threads)
                    .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                        try{
                            userService.chargePointWithLock(1L, amount, "REQ-" + i);
                            return true;
                        }catch(Exception ex){
                            System.out.println(ex);
                            return false;
                        }
                    }, executor))
                    .toList();

            // Wait for all to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // Verify final balance
            Users updated = userService.getUser(1L);
            BigDecimal expected = BigDecimal.valueOf(1000).add(amount.multiply(BigDecimal.valueOf(threads)));
            assertThat(updated.getBalance()).isEqualByComparingTo(expected);
        }

        @Test
        @Sql("classpath:sql/lock/UserPoint.sql")
        @DisplayName("Concurrent payPointWithLock should deduct balance correctly")
        void concurrentPayPointWithLock() {
            int threads = 5;
            BigDecimal chargeAmount = BigDecimal.valueOf(200);
            BigDecimal useAmount = BigDecimal.valueOf(50);

            // Pre-charge to ensure sufficient balance
            userService.chargePointWithLock(1L, chargeAmount, "INIT-REQ");

            // Perform concurrent use operations
            ExecutorService executor = Executors.newFixedThreadPool(20);
            // Perform concurrent charge operations
            List<CompletableFuture<Boolean>> futures = IntStream.range(0, threads)
                    .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                        try{
                            userService.payPointWithLock(1L, useAmount, "USE-" + i);
                            return true;
                        }catch(Exception ex){
                            return false;
                        }
                    }, executor))
                    .toList();

            // Wait for all to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // Verify final balance
            Users updated = userService.getUser(1L);
            BigDecimal initial = BigDecimal.valueOf(1000).add(chargeAmount);
            BigDecimal expected = initial.subtract(useAmount.multiply(BigDecimal.valueOf(threads)));
            assertThat(updated.getBalance()).isEqualByComparingTo(expected);
        }
    }
}
