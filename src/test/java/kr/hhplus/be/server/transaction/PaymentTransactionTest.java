package kr.hhplus.be.server.transaction;


import kr.hhplus.be.server.TestcontainersConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
public class PaymentTransactionTest {


    @Nested
    @DisplayName("재고 관련 트랜잭션 확인 테스트")
    @Sql("classpath:sql/transaction/inventory/OutOfStock.sql")
    class InventoryTransaction{

        @Test
        void test_container_test(){
            //assertThat(1).isEqualTo(1);
        }
    }
}
