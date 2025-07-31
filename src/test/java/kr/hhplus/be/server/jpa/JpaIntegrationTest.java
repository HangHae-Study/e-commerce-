package kr.hhplus.be.server.jpa;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.adapter.entity.CouponIssueJpaEntity;
import kr.hhplus.be.server.domain.coupon.adapter.entity.CouponJpaEntity;
import kr.hhplus.be.server.domain.coupon.adapter.repository.CouponIssueJpaRepository;
import kr.hhplus.be.server.domain.coupon.adapter.repository.CouponJpaRepository;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import kr.hhplus.be.server.domain.order.adapter.entity.OrderJpaEntity;
import kr.hhplus.be.server.domain.order.adapter.repository.OrderJpaRepository;
import kr.hhplus.be.server.domain.order.adapter.repository.OrderLineJpaRepository;
import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.OrderLine;
import kr.hhplus.be.server.domain.payment.adapter.entity.PaymentJpaEntity;
import kr.hhplus.be.server.domain.payment.adapter.repository.PaymentJpaRepository;
import kr.hhplus.be.server.domain.payment.application.Payment;
import kr.hhplus.be.server.domain.product.adapter.entity.ProductJpaEntity;
import kr.hhplus.be.server.domain.product.adapter.entity.ProductLineJpaEntity;
import kr.hhplus.be.server.domain.product.adapter.repository.ProductJpaRepository;
import kr.hhplus.be.server.domain.product.adapter.repository.ProductLineJpaRepository;
import kr.hhplus.be.server.domain.user.adapter.repository.PointJpaRepository;
import kr.hhplus.be.server.domain.user.adapter.repository.UsersJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = {
        "classpath:sql/cleanup.sql",
        "classpath:sql/schema.sql",
        "classpath:sql/single.jpa/integrated_jpa_scen.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
public class JpaIntegrationTest {
    @Autowired private PointJpaRepository pointRepo;
    @Autowired private ProductJpaRepository productRepo;
    @Autowired
    private ProductLineJpaRepository productLineRepo;
    @Autowired private CouponJpaRepository couponRepo;
    @Autowired private CouponIssueJpaRepository couponIssueRepo;
    @Autowired private OrderJpaRepository orderRepo;
    @Autowired private OrderLineJpaRepository orderLineRepo;
    @Autowired private PaymentJpaRepository paymentRepo;
    @Autowired private UsersJpaRepository usersRepo;

    @Test @DisplayName("1. 충분한 포인트가 있는 유저 조회")
    void testScenario1_pointLoaded() {
        var pt = pointRepo.findById(1L).orElseThrow();
        assertThat(pt.getUserId()).isEqualTo(1L);
        assertThat(pt.getBalance()).isEqualByComparingTo(new BigDecimal("1000.00"));
    }

    @Test @DisplayName("2. 상품 목록 10개 조회")
    void testScenario2_fetchProducts() {
        var prods = productRepo.findAll();
        assertThat(prods).hasSize(10);
    }

    @Test @DisplayName("3. 상품 ID=1의 라인 아이템 5개 조회")
    void testScenario3_productLines() {
        List<ProductLineJpaEntity> lines = productLineRepo.findAll().stream()
                .filter(pl -> pl.getProductId().equals(1L))
                .collect(Collectors.toList());
        assertThat(lines).hasSize(5);
    }

    @Test @DisplayName("4. 쿠폰 목록 10개, remaining 모두 서로 다름")
    void testScenario4_fetchCoupons() {
        var coupons = couponRepo.findAll();
        assertThat(coupons).hasSize(10);
        long distinctRemain = coupons.stream()
                .map(CouponJpaEntity::getRemaining)
                .distinct().count();
        assertThat(distinctRemain).isEqualTo(10);
    }

    @Test @DisplayName("5. 쿠폰 발급 요청 → JPA 저장")
    void testScenario5_issueCoupon() {
        var domain = CouponIssue.builder()
                .couponCode("NEW123")
                .couponId(1L)
                .userId(1L)
                .couponValid("Y")
                .discountRate(new BigDecimal("10.00"))
                .expireDate(LocalDateTime.parse("2025-12-31T23:59:59"))
                .updateDt(LocalDateTime.now())
                .build();

        var saved = couponIssueRepo.save(CouponIssueJpaEntity.fromDomain(domain));
        assertThat(saved.getCouponIssueId()).isNotNull();
        assertThat(saved.getCouponCode()).isEqualTo("NEW123");
    }

    @Test @DisplayName("6. 유저 발급 쿠폰 2개 조회")
    void testScenario6_userCouponIssues() {
        var list = couponIssueRepo.findAll().stream()
                .filter(ci -> ci.getUserId().equals(1L))
                .toList();
        assertThat(list).hasSize(2);
    }

    @Test @DisplayName("7. 주문 생성 → JPA 저장 & 매핑 확인")
    void testScenario7_createOrder() {
        var line1 = OrderLine.builder()
                .userId(1L).productLineId(1L)
                .orderLinePrice(new BigDecimal("50.00")).quantity(1)
                .couponYn("N").status("O_MAKE")
                .orderDt(LocalDateTime.now()).updateDt(LocalDateTime.now())
                .build();
        var line2 = OrderLine.builder()
                .userId(1L).productLineId(2L)
                .orderLinePrice(new BigDecimal("75.00")).quantity(2)
                .couponYn("N").status("O_MAKE")
                .orderDt(LocalDateTime.now()).updateDt(LocalDateTime.now())
                .build();

        var orderDom = Order.builder()
                .orderCode("TEST-007")
                .userId(1L)
                .totalPrice(new BigDecimal("200.00"))
                .orderLines(List.of(line1, line2))
                .orderDt(LocalDateTime.now())
                .status("O_MAKE")
                .updateDt(LocalDateTime.now())
                .build();

        var saved = orderRepo.save(OrderJpaEntity.fromDomain(orderDom));
        assertThat(saved.getOrderId()).isNotNull();
        assertThat(saved.getOrderLines()).hasSize(2);
    }

    @Test @DisplayName("8. 저장된 주문+라인 조회")
    void testScenario8_loadOrder() {
        var ord = orderRepo.findById(100L).orElseThrow();
        assertThat(ord.getOrderCode()).isEqualTo("ORD-008");
        assertThat(ord.getOrderLines()).hasSize(2);
    }

    @Test @DisplayName("9. 결제 정보 저장 → 조회")
    void testScenario9_paymentFlow() {
        var payDom = Payment.of(1L, 100L, new BigDecimal("150.00"), "P_CMPL");
        var saved  = paymentRepo.save(PaymentJpaEntity.fromDomain(payDom));

        var got = paymentRepo.findById(saved.getPaymentId()).orElseThrow();
        assertThat(got.getStatus()).isEqualTo("P_CMPL");
        assertThat(got.getTotalPrice()).isEqualByComparingTo(new BigDecimal("150.00"));
    }
}
