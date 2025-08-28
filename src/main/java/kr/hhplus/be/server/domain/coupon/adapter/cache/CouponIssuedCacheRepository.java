package kr.hhplus.be.server.domain.coupon.adapter.cache;

import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

import static kr.hhplus.be.server.domain.coupon.adapter.cache.CouponCacheKeyProvider.CouponClaimStatus.INIT;
import static kr.hhplus.be.server.domain.coupon.adapter.cache.CouponCacheKeyProvider.CouponClaimStatus.ISSUED;

@Repository
@RequiredArgsConstructor
public class CouponIssuedCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final CouponCacheKeyProvider key;

    // 유저 발급 요청 상태 확인
    public Optional<CouponCacheKeyProvider.CouponClaimStatus> getClaimStatus(Long couponId, Long userId) {
        String v = stringRedisTemplate.opsForValue().get(key.issued(couponId, userId));
        if (v == null) return Optional.of(INIT);
        try {
            return Optional.of(CouponCacheKeyProvider.CouponClaimStatus.valueOf(v.toUpperCase()));
        } catch (IllegalArgumentException e) {
            // 예상치 못한 값이 들어간 경우 보호
            return Optional.of(INIT);
        }
    }

    // 유져 쿠폰 발급 상태를 최종 확정 (ISSUED/FAILED). 이미 최종 상태면 덮어쓰지 않음
    public void finalizeClaimStatus(Long couponId, Long userId, Duration ttl, CouponIssue issue) {
        String sKey = key.issued(couponId, userId);
        // 이미 값이 있으면 바꾸지 않되, WAITED/PROCESSING 등 중간값만 덮어쓰고 싶다면 조건부 로직 추가 가능
        String cur = stringRedisTemplate.opsForValue().get(sKey);
        if (cur == null || "WAITED".equalsIgnoreCase(cur) || "PROCESSING".equalsIgnoreCase(cur)) {
            stringRedisTemplate.opsForValue().set(sKey, String.valueOf(ISSUED), ttl);
        }

        if(issue != null){
            cacheCouponIssuedData(couponId, userId, issue, ttl);
        }
    }

    public void cacheCouponIssuedData(Long couponId, Long userId, CouponIssue issue, Duration ttl) {
        // 도메인 객체도 함께 저장
        String issueKey = key.issueDetail(couponId, userId); // 별도 키 제공 필요
        redisTemplate.opsForValue().set(issueKey, issue, ttl);
    }

    // 저장된 CouponIssue 조회
    public Optional<CouponIssue> getIssuedCoupon(Long couponId, Long userId) {
        String issueKey = key.issueDetail(couponId, userId);
        Object val = redisTemplate.opsForValue().get(issueKey);
        if (val instanceof CouponIssue ci) {
            return Optional.of(ci);
        }
        return Optional.empty();
    }

    // 유저 쿠폰 발급 상태를 PROCESSING 으로 세팅(선점) — 이미 최종 상태면 선점 실패
    public boolean markProcessingIfAllowed(Long couponId, Long userId, Duration ttl) {
        String sKey = key.issued(couponId, userId);
        String v = stringRedisTemplate.opsForValue().get(sKey);
        if (v == null || "WAITED".equalsIgnoreCase(v)) {
            // WAITED → PROCESSING
            stringRedisTemplate.opsForValue().set(sKey, "PROCESSING", ttl);
            return true;
        }
        // 이미 ISSUED/FAILED/PROCESSING 이면 false
        return false;
    }
}
