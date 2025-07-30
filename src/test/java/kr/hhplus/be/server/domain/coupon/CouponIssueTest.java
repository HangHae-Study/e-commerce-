package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import kr.hhplus.be.server.domain.coupon.application.repository.CouponIssueRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

public class CouponIssueTest {

    @Nested
    @DisplayName("쿠폰 발급 도메인 단위 테스트")
    class CouponIssueUnitTest{
        @Test
        void 유효한_발급_쿠폰() {
            var issue = CouponIssueTestInstance.simpleValidIssue();
            assertTrue(issue.isValid());
        }

        @Test
        void 발급_쿠폰_백분율(){
            var issue = CouponIssueTestInstance.simpleValidIssue();

            BigDecimal rate = issue.getDiscountRate();
            rate = rate.divide(BigDecimal.valueOf(100L)).setScale(0, RoundingMode.HALF_UP);

            assertEquals(rate, issue.getDiscountRateRatio());
        }

        @Test
        void 유효하지않은_발급_쿠폰() {
            var issue = CouponIssueTestInstance.expiredIssue();
            assertFalse(issue.isValid());
        }

        @Test
        void 발급_쿠폰_사용() {
            var issue = CouponIssueTestInstance.simpleValidIssue();
            issue.used();
            assertEquals("N", issue.getCouponValid());
        }

        @Test
        void 발급_쿠폰_사용_완료() {
            var issue = CouponIssueTestInstance.usedIssue();
            assertFalse(issue.isValid());
        }


        @Test
        void 발급_쿠폰_사용_실패() {
            var issue = CouponIssueTestInstance.usedIssue();
            assertThrows(IllegalStateException.class, issue::used);
        }
    }

    @SpringBootTest
    @Nested
    @DisplayName("발급 쿠폰 Repository 테스트")
    class CouponIssueRepositoryTest{
        @Autowired
        private CouponIssueRepository couponIssueRepository;

        private CouponIssue buildIssue(String code) {
            return CouponIssue.builder()
                    .couponCode(code)
                    .couponId(100L)
                    .userId(200L)
                    .couponValid("Y")
                    .discountRate(new BigDecimal("0.15"))
                    .expireDate(LocalDateTime.now().plusDays(1))
                    .updateDt(LocalDateTime.now())
                    .build();
        }

        @Test
        void 쿠폰_ID기반_발급_및_조회(){
            // given
            CouponIssue toSave = buildIssue("ABC123");

            // when
            CouponIssue saved = couponIssueRepository.save(toSave);
            Optional<CouponIssue> loaded = couponIssueRepository.findById(saved.getCouponIssueId());

            // then
            assertThat(loaded)
                    .isPresent()
                    .get()
                    .extracting(
                            CouponIssue::getCouponIssueId,
                            CouponIssue::getCouponCode,
                            CouponIssue::getCouponId,
                            CouponIssue::getUserId
                    )
                    .containsExactly(
                            saved.getCouponIssueId(),
                            "ABC123",
                            100L,
                            200L
                    );
        }

        @Test
        void 미발급_쿠폰_ID기반_조회() {
            assertThat(couponIssueRepository.findById(99999L)).isEmpty();
        }

        @Test
        void 발급_쿠폰_코드_조회() {
            // given
            CouponIssue saved = couponIssueRepository.save(buildIssue("XYZ999"));

            // when
            Optional<CouponIssue> found = couponIssueRepository.findByCouponCode("XYZ999");

            // then
            assertThat(found)
                    .isPresent()
                    .get()
                    .extracting(
                            CouponIssue::getCouponIssueId,
                            CouponIssue::getCouponCode
                    )
                    .containsExactly(
                            saved.getCouponIssueId(),
                            "XYZ999"
                    );
        }

        @Test
        void 미발급_쿠폰_코드_조회() {
            assertThat(couponIssueRepository.findByCouponCode("NOPE")).isEmpty();
        }
    }

}
