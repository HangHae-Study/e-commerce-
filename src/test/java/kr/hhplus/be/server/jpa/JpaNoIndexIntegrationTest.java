package kr.hhplus.be.server.jpa;

import kr.hhplus.be.server.domain.coupon.adapter.repository.CouponIssueJpaRepository;
import kr.hhplus.be.server.domain.order.adapter.projection.BestSellingProductLineProjection;
import kr.hhplus.be.server.domain.order.adapter.repository.OrderLineJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = {
        "classpath:sql/cleanup.sql",
        "classpath:sql/schema.sql",
        "classpath:sql/index.jpa/noindex_scan.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class JpaNoIndexIntegrationTest {

    @Autowired
    OrderLineJpaRepository orderLineRepo;
    @Autowired
    CouponIssueJpaRepository couponIssueRepo;

    private final LocalDate START = LocalDate.parse("2025-07-29");
    private final LocalDate END = LocalDate.parse("2025-08-01");

    @Test
    @DisplayName("1. 최근 3일 완료 주문의 Top-5 판매량 상품라인")
    void testTop5NoJoin() {
        List<BestSellingProductLineProjection> list =
                orderLineRepo.findTop5ByOrderDtBetween(START, END);

        // 스크립트상 합계: pl1= (2+1)=3, pl3=(4+2)=6, pl2=3
        // 순위: pl3(6), pl2(3), pl1(3) → LIMIT5 이므로 세 개 모두 나옴
        assertThat(list).hasSize(3);
        assertThat(list.get(0).getProductLineId()).isEqualTo(3L);
        assertThat(list.get(0).getTotalQuantity()).isEqualTo(6L);
    }
}
