package kr.hhplus.be.server.domain.coupon.adapter.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.zset.Tuple;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CouponCacheQueue {
    private final StringRedisTemplate stringRedisTemplate;
    private final CouponCacheKeyProvider key;

    // 쿠폰 대기열 등록
    public boolean enqueueClaim(Long couponId, Long userId) {
        return enqueueClaim(couponId, userId, System.currentTimeMillis());
    }

    public boolean enqueueClaim(Long couponId, Long userId, long nowMillis) {
        String qKey = key.q(couponId);
        String member = String.valueOf(userId);
        Boolean added = stringRedisTemplate.opsForZSet().addIfAbsent(qKey, member, nowMillis);

        // 상태 키는 WAITED로 초기화(이미 다른 상태면 건들지 않음)
        stringRedisTemplate.opsForValue()
                .setIfAbsent(key.issued(couponId, userId), CouponCacheKeyProvider.CouponClaimStatus.WAITED.name());

        return Boolean.TRUE.equals(added);
    }

    // 대기열에서 등수 확인
    public Long getRank(Long couponId, Long userId) {
        String qKey = key.q(couponId);
        Long r0 = stringRedisTemplate.opsForZSet().rank(qKey, String.valueOf(userId));
        return (r0 == null) ? -1L : r0 + 1;
    }


    // 큐 현재 사이즈 확인
    public long getQueueSize(Long couponId) {
        String qKey = key.q(couponId); // "q:coupon:{couponId}"
        Long size = stringRedisTemplate.opsForZSet().size(qKey);
        return size != null ? size : 0L;
    }

    // 대기열에서 순차처리 위한 요청 뽑기
    public List<String> popOldestMembers(Long couponId, Long count) {
        String qKey = key.q(couponId);
        ZSetOperations.TypedTuple<String> tuple = stringRedisTemplate.opsForZSet().popMin(qKey);

        List<String> nextUser = new ArrayList<>();

        nextUser.add(tuple.getValue());

        return nextUser;
    }
}
