package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.application.Coupon;
import kr.hhplus.be.server.domain.coupon.application.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.testinstance.CouponTestInstance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CouponTest {

    @Nested
    @DisplayName("쿠폰 도메인 단위 테스트")
    class CouponDomainUnitTest{
        @Test
        void 잔여량이_충분한_쿠폰() {
            var coupon = CouponTestInstance.simpleCoupon();
            assertTrue(coupon.hasRemaining());
        }

        @Test
        void 잔여량이_부족한_쿠폰() {
            var coupon = CouponTestInstance.couponWithNoRemaining();
            assertFalse(coupon.hasRemaining());
        }

        @Test
        void 잔여량이_충분한_쿠폰_감소() {
            var coupon = CouponTestInstance.simpleCoupon();
            coupon.decrease();
            assertEquals(0L, coupon.getRemaining());
        }

        @Test
        void 잔여량이_부족한_쿠폰_감소() {
            var coupon = CouponTestInstance.couponWithNoRemaining();
            assertThrows(IllegalStateException.class, coupon::decrease);
        }
    }

    @Nested
    @DisplayName("쿠폰 레포지토리 단위 테스트")
    class CouponRepositoryTest{
        @Autowired
        private CouponRepository couponRepository;

        @Test
        void 쿠폰_ID_기반_조회(){
            // given
            Coupon coupon = CouponTestInstance.simpleCoupon();

            // when
            Coupon saved = couponRepository.save(coupon);

            // then
            assertThat(saved.getCouponId()).isNotNull();
            Optional<Coupon> found = couponRepository.findById(saved.getCouponId());
            assertThat(found)
                    .isPresent()
                    .get()
                    .extracting(Coupon::getCouponId, Coupon::getDiscountRate)
                    .containsExactly(saved.getCouponId(), saved.getDiscountRate());
        }

        @Test
        void 쿠폰_ID_미존재(){
            Optional<Coupon> found = couponRepository.findById(99999L);
            assertThat(found).isEmpty();
        }

        @Test
        void 유효한_쿠폰_목록_조회() {
            // given
            // 유효한 쿠폰: expireDate = now +1일, remaining = 5
            Coupon valid1 = CouponTestInstance.simpleCouponBuilder()
                    .expireDate(LocalDateTime.now().plusDays(1))
                    .remaining(5L)
                    .build();
            // 만료된 쿠폰: expireDate = now -1일, remaining = 5
            Coupon expired = CouponTestInstance.simpleCouponBuilder()
                    .expireDate(LocalDateTime.now().minusDays(1))
                    .remaining(5L)
                    .build();
            // 재고 소진 쿠폰: expireDate = now +1일, remaining = 0
            Coupon soldOut = CouponTestInstance.simpleCouponBuilder()
                    .expireDate(LocalDateTime.now().plusDays(1))
                    .remaining(0L)
                    .build();

            Coupon savedValid1 = couponRepository.save(valid1);
            couponRepository.save(expired);
            couponRepository.save(soldOut);

            // when
            List<Coupon> validCoupons = couponRepository.findValidCouponList(LocalDateTime.now(), 0L);

            // then
            assertThat(validCoupons)
                    .hasSize(1)
                    .first()
                    .extracting(Coupon::getCouponId, Coupon::getRemaining)
                    .containsExactly(savedValid1.getCouponId(), 5L);
        }

        @Test
        void 유효한_쿠폰_없음() {
            // given: 모두 만료되거나 재고 0
            couponRepository.save(CouponTestInstance.simpleCouponBuilder()
                    .expireDate(LocalDateTime.now().minusHours(1))
                    .remaining(5L).build());
            couponRepository.save(CouponTestInstance.simpleCouponBuilder()
                    .expireDate(LocalDateTime.now().plusHours(1))
                    .remaining(0L).build());

            // when
            List<Coupon> validCoupons = couponRepository.findValidCouponList(LocalDateTime.now(), 0L);

            // then
            assertThat(validCoupons).isEmpty();
        }
    }


    /*
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponIssueRepository couponIssueRepository;

    private Long couponId;
    private Long userId;

    @BeforeEach
    void setUp() {
        // 1) 테스트용 쿠폰을 미리 저장
        Coupon couponDomain = Coupon.builder()
                .totalIssued(5L)
                .remaining(5L)
                .discountRate(new BigDecimal("20"))
                .expireDate(LocalDateTime.now().plusDays(7))
                .updateDt(LocalDateTime.now())
                .build();

        Coupon couponEntity = couponRepository.save(
                couponDomain
        );
        couponId = couponEntity.getCouponId();
        userId = 1L;
    }

    @Test
    void issueCoupon_success() throws Exception {
        // 2) 발급 요청
        String requestJson = String.format("{\"userId\": %d , \"couponId\":%d}", userId, couponId);

        mockMvc.perform(post("/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.couponIssueId").isNumber())
                .andExpect(jsonPath("$.data.couponId").value(couponId));
    }

     */
}
