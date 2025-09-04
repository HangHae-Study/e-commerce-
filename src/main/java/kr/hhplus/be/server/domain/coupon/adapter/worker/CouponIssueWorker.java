package kr.hhplus.be.server.domain.coupon.adapter.worker;

import kr.hhplus.be.server.domain.coupon.adapter.cache.CouponCacheQueue;
import kr.hhplus.be.server.domain.coupon.adapter.cache.CouponCacheRepository;
import kr.hhplus.be.server.domain.coupon.adapter.cache.CouponIssuedCacheRepository;
import kr.hhplus.be.server.domain.coupon.adapter.generator.DefaultCouponCodeGenerator;
import kr.hhplus.be.server.domain.coupon.application.Coupon;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponIssueWorker {

    private final CouponCacheQueue queue;
    private final CouponCacheRepository couponCacheRepository;
    private final CouponIssuedCacheRepository issuedCacheRepository;
    private final DefaultCouponCodeGenerator codeGenerator;

    public void processCouponIssue(Long userId, Long couponId){
        // Redis에서 원자적 차감
        Long stock = couponCacheRepository.tryConsumeStock(couponId);
        if (stock < 0L) {
            // 잔여 수량 없음 → 실패 처리
            issuedCacheRepository.finalizeClaimStatus(
                    couponId, userId, Duration.ofMinutes(5), null
            );
            log.warn("쿠폰 [{}] - 사용자 [{}] 발급 실패 (재고 부족)", couponId, userId);
            return;
        }


    }

    /**
     * 매 1초마다 쿠폰 발급 워커 실행
     * (cron 표현식: 초 분 시 일 월 요일)
     * "0/1 * * * * *" → 매초 실행
     */
    //@Scheduled(cron = "0/1 * * * * *")
    public void processCouponIssueQueue(Long couponId) {
        // 발급 대기열에서 최대 1명 꺼내기
        List<String> userIds = queue.popOldestMembers(couponId, 1L);
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        log.info("쿠폰 [{}] 대기열에서 {}명 꺼냄", couponId, userIds.size());

        for (String userIdStr : userIds) {
            Long userId = Long.valueOf(userIdStr);

            // 재고 확인 & 차감
            Long success = couponCacheRepository.tryConsumeStock(couponId);
            if (success < 0L) {
                // 잔여 수량 없음 → 실패 처리
                issuedCacheRepository.finalizeClaimStatus(
                        couponId, userId, Duration.ofMinutes(5), null
                );
                log.warn("쿠폰 [{}] - 사용자 [{}] 발급 실패(재고 부족)", couponId, userId);
                continue;
            }

            // 쿠폰 도메인 정보 조회
            Coupon coupon = couponCacheRepository.getCoupon(couponId);

            // CouponIssue 도메인 객체 생성
            CouponIssue issue = CouponIssue.builder()
                    //.couponIssueId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE) // JPA 가 만들어줄것
                    .couponCode(codeGenerator.generate(coupon, userId, success))
                    .couponId(couponId)
                    .userId(userId)
                    .couponValid("Y")
                    .discountRate(BigDecimal.valueOf(10)) // 샘플 값
                    .expireDate(coupon.getExpireDate())
                    .updateDt(LocalDateTime.now())
                    .build();

            // 발급 확정 + 캐시 저장
            issuedCacheRepository.finalizeClaimStatus(
                    couponId, userId, Duration.ofMinutes(5), issue
            );

            log.info("쿠폰 [{}] - 사용자 [{}] 발급 완료 → {}", couponId, userId, issue.getCouponCode());
        }
    }
}
