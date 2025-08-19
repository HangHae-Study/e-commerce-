package kr.hhplus.be.server.domain.product.adapter.cache;

import kr.hhplus.be.server.domain.order.command.TopOrderProductCommand;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.command.ProductRankingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class TopProductCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;


    // 실시간(일자별) ZSET: zs:rank:product:realtime:YYYY-MM-DD
    private static final String REALTIME_KEY_PREFIX          = "zs:rank:product:realtime";
    // 3일 합산 ZUNION 결과 ZSET: zs:rank:product:sum:top5:3days:{start}:{end}
    private static final String FOR_3_DAYS_RANK_KEY_PREFIX   = "zs:rank:product:sum:top5:3days";
    // 3일 합산 최종 응답값(예: List<ProductLine>) 캐시: s:cache:rank:product:sum:top5:3days:{start}:{end}
    private static final String FOR_3_DAYS_CACHE_KEY_PREFIX  = "s:cache:rank:product:sum:top5:3days";


    /*
    키 빌더
     */
    private String realtimeKey(LocalDate d) {
        // ex) zs:rank:product:realtime:2025-08-19
        return REALTIME_KEY_PREFIX + ":" + d;
    }

    private String threeDaysZsetKey(LocalDate start, LocalDate end) {
        return FOR_3_DAYS_RANK_KEY_PREFIX + ":" + start + ":" + end;
    }

    private String threeDaysValueCacheKey(LocalDate start, LocalDate end) {
        return FOR_3_DAYS_CACHE_KEY_PREFIX + ":" + start + ":" + end;
    }

    private void setExpireIfAbsent(String key, Duration duration){
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if(ttl == null || ttl == -1){
            redisTemplate.expire(key, duration);
        }
    }

    @SuppressWarnings("unchecked")
    public List<ProductLine> find(LocalDate start, LocalDate end) {
        String key = threeDaysValueCacheKey(start, end);
        Object val = redisTemplate.opsForValue().get(key);
        if (val == null) return null;
        return (List<ProductLine>) val;
    }

    public void save(LocalDate start, LocalDate end,
                     List<ProductLine> value,
                     Duration ttl) {
        String key = threeDaysValueCacheKey(start, end);
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    public void evict(LocalDate start, LocalDate end) {
        redisTemplate.delete(threeDaysValueCacheKey(start, end));
    }

     //1) 주문 성공 시 실시간 ZSET 점수 증가
    public void increaseTodayRankScores(List<ProductRankingDto.ProductItemForRank> items){
        items.forEach(pl -> {
            redisTemplate.opsForZSet().incrementScore(
                    realtimeKey(LocalDate.now()),
                    pl.productLineId(),
                    pl.quantity()
            );

        });

        setExpireIfAbsent(realtimeKey(LocalDate.now()), Duration.ofDays(7));
    }

     //2) 최근 3일 합산 TOP5 - ZUNIONSTORE 수행 후 결과 반환
     public List<ProductRankingDto.ProductRankEntry> topFiveForThreeDaysZset() {
         LocalDate end   = LocalDate.now();
         LocalDate start = end.minusDays(3);

         List<String> keys = start.datesUntil(end)
                 .map(this::realtimeKey)
                 .toList();

         if (keys.isEmpty()) return List.of();

         String destKey = threeDaysZsetKey(start, end);

         // unionAndStore: 첫 번째를 기준키, 나머지는 리스트로
         stringRedisTemplate.opsForZSet().unionAndStore(
                 keys.get(0),
                 keys.subList(1, keys.size()),
                 destKey
         );

         // 결과 ZSET에 짧은 TTL(예: 10분). 이미 있으면 갱신하지 않음.
         setExpireIfAbsent(destKey, Duration.ofMinutes(3600));

         Set<ZSetOperations.TypedTuple<String>> tuples =
                 stringRedisTemplate.opsForZSet().reverseRangeWithScores(destKey, 0, 4);

         if (tuples == null || tuples.isEmpty()) return List.of();

         return tuples.stream()
                 .map(t -> new ProductRankingDto.ProductRankEntry(Long.valueOf(t.getValue()), t.getScore().intValue()))
                 .toList();
     }
}
