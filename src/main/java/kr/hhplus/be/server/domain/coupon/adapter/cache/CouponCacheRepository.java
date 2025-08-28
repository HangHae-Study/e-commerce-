package kr.hhplus.be.server.domain.coupon.adapter.cache;

import kr.hhplus.be.server.domain.coupon.application.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final CouponCacheKeyProvider key;

    private static byte[] b(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    // 쿠폰 도메인 정보 초기 캐싱(무조건 해당 메서드 호출 후 추후 프로세스진행)
    public void cachingCoupon(Coupon coupon) {
        if (coupon == null || coupon.getCouponId() == null) throw new IllegalArgumentException();

        String metaKey = key.meta(coupon.getCouponId());
        String stockKey = key.stock(coupon.getCouponId());

        if (coupon.getExpireDate() == null) {
            throw new IllegalArgumentException();
        }

        Duration ttl = Duration.between(LocalDateTime.now(), coupon.getExpireDate());
        if (ttl.isNegative() || ttl.isZero()){

        }else{
            // metadata
            redisTemplate.opsForValue().set(metaKey, coupon, ttl);
            // 재고 decr 수량 정보
            stringRedisTemplate.opsForValue().setIfAbsent(stockKey, String.valueOf(coupon.getTotalIssued()));
        }
    }

    // 쿠폰 캐시 데이터 조회
    @SuppressWarnings("unchecked")
    public Coupon getCoupon(Long couponId) {
        Object v = redisTemplate.opsForValue().get(key.meta(couponId));
        return (Coupon) v;
    }


    // 발급하려는 쿠폰의 잔여수량 확인 (ZSET 기반)
    public long getRemainingStock(Long couponId) {
        String s = stringRedisTemplate.opsForValue().get(key.stock(couponId));
        if (s == null) return -1L;
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return -1L;
        }
    }

    // 재고 1 감소. 감소 후 값(>=0)이면 성공, 음수면 실패로 간주하고 즉시 복구
    public Long tryConsumeStock(Long couponId) {
        String stockKey = key.stock(couponId);
        Long after = stringRedisTemplate.opsForValue().decrement(stockKey);
        if (after == null) return -99L; // 키 없음 → 실패로 간주
        if (after < 0) {
            // 재고 초과로 감소됨 → 즉시 복구
            stringRedisTemplate.opsForValue().increment(stockKey);
            return -99L;
        }
        return after;
    }


}
