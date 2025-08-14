package kr.hhplus.be.server.cache;

import kr.hhplus.be.server.domain.coupon.adapter.repository.CouponIssueJpaRepository;
import kr.hhplus.be.server.domain.order.adapter.projection.BestSellingProductLineProjection;
import kr.hhplus.be.server.domain.order.adapter.repository.OrderLineJpaRepository;
import kr.hhplus.be.server.domain.order.application.service.OrderService;
import kr.hhplus.be.server.domain.order.command.TopOrderProductCommand;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.application.facade.ProductFacade;
import kr.hhplus.be.server.domain.product.application.repository.ProductLineRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Sql(scripts = {
        "classpath:sql/cleanup.sql",
        "classpath:sql/schema.sql",
        "classpath:sql/cache/august_100_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TopProductCacheTest {

    @Autowired
    OrderLineJpaRepository orderLineRepo;

    @Autowired
    ProductLineRepository productLineRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    ProductFacade productFacade;

    private final LocalDate START = LocalDate.now().minusDays(3);
    private final LocalDate END = LocalDate.now().minusDays(1);

    @Test
    @DisplayName("0. 데이터 정상 생성 테스트")
    void testDataInput(){
        List<ProductLine> plList = productLineRepository.findAll();

        assertThat(plList).isNotEmpty();

        System.out.println(plList);
    }

    @Test
    @DisplayName("1. 최근 3일 완료 주문의 Top-5 판매량 상품라인")
    void testTop5NoJoin() {
        List<BestSellingProductLineProjection> list =
                orderLineRepo.findTop5ByOrderDtBetween(START, END);

        assertThat(list).isNotEmpty();
        assertThat(list).hasSizeLessThanOrEqualTo(5);

        System.out.println(list);
    }

    @Test
    @DisplayName("2. 주문 완료 정보 중 상품라인ID추출")
    void getProductLineIdFromOrderLine() {
        List<TopOrderProductCommand.TopOrderProductResponse> list =
                orderService.getTopOrderProduct(START, END);

        assertThat(list).isNotEmpty();
        assertThat(list).hasSizeLessThanOrEqualTo(5);

        System.out.println(list);
    }

    @ParameterizedTest(name = "[{index}] start={0}, end={1}")
    @CsvSource({
            "2025-08-01, 2025-08-03",
            "2025-08-10, 2025-08-15",
            "2025-08-20, 2025-08-25",
            "2025-08-28, 2025-08-31"
    })
    @DisplayName("3. 상위 상품 조회 캐시 확인")
    void getProductLineByCache(String dateS, String dateE ){
        LocalDate start = LocalDate.parse(dateS);
        LocalDate end = LocalDate.parse(dateE);

        /* 캐시 미스 예외 처리 전
        assertThrows(NoSuchElementException.class, () -> {
            productFacade.getTopProductItems(start, end);
        });
        */

        List<ProductLine> topItems = productFacade.getTopProductItems(start, end);

        assertThat(topItems).isNotEmpty();
        assertThat(topItems).hasSizeLessThanOrEqualTo(5);

        System.out.println(topItems);
    }
}
