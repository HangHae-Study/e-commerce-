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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class TopProductCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final ProductCacheKeyProvider keyProvider;

    private void setExpireIfAbsent(String key, Duration duration){
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if(ttl == null || ttl == -1){
            redisTemplate.expire(key, duration);
        }
    }

    private Duration ttlUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
        return Duration.between(now, midnight);
    }

    public boolean getLockForTop5RankFor3Days(LocalDate start, LocalDate end){
        String lockKey = keyProvider.lockCacheKey(start, end);

        return Boolean.TRUE.equals(
                stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "1", Duration.ofSeconds(10))
        );
    }


    @SuppressWarnings("unchecked")
    public List<ProductLine> findTop5RankFor3Days(LocalDate start, LocalDate end) {
        String key = keyProvider.threeDaysValueCacheKey(start, end);
        Object val = redisTemplate.opsForValue().get(key);
        if (val == null) return null;
        return (List<ProductLine>) val;
    }

    public void saveTop5RankFor3Days(LocalDate start, LocalDate end,
                                     List<ProductLine> value) {
        String key = keyProvider.threeDaysValueCacheKey(start, end);
        redisTemplate.opsForValue().set(key, value, ttlUntilMidnight());
    }

    public void evict(LocalDate start, LocalDate end) {
        redisTemplate.delete(keyProvider.threeDaysValueCacheKey(start, end));
    }

     //1) 주문 성공 시 실시간 ZSET 점수 증가
    public void increaseTodayRankScores(LocalDate day, List<ProductRankingDto.ProductItemForRank> items){
        items.forEach(pl -> {
            redisTemplate.opsForZSet().incrementScore(
                    keyProvider.realtimeKey(day),
                    pl.productLineId(),
                    pl.quantity()
            );

        });

        setExpireIfAbsent(keyProvider.realtimeKey(LocalDate.now()), Duration.ofDays(7));
    }

     //2) 최근 3일 합산 TOP5 - ZUNIONSTORE 수행 후 결과 반환
    public List<ProductRankingDto.ProductRankEntry> topFiveForThreeDaysZset() {
        LocalDate end   = LocalDate.now().minusDays(1);
        LocalDate start = end.minusDays(2);

        List<String> keys = start.datesUntil(end)
             .map(keyProvider::realtimeKey)
             .toList();

        if (keys.isEmpty()) return List.of();

        String destKey = keyProvider.threeDaysZsetKey(start, end);

        // unionAndStore: 첫 번째를 기준키, 나머지는 리스트로
        stringRedisTemplate.opsForZSet().unionAndStore(
             keys.get(0),
             keys.subList(1, keys.size()),
             destKey
        );

        setExpireIfAbsent(destKey, ttlUntilMidnight());

        Set<ZSetOperations.TypedTuple<String>> tuples =
             stringRedisTemplate.opsForZSet().reverseRangeWithScores(destKey, 0, 4);

        if (tuples == null || tuples.isEmpty()) return List.of();

        return tuples.stream()
             .map(t -> new ProductRankingDto.ProductRankEntry(Long.valueOf(t.getValue()), t.getScore().intValue()))
             .toList();
    }

    // 3) rank 미 존재시 zset 초기화
    public void refreshThreeDaysZsetFromDb(LocalDate start,
                                           LocalDate end,
                                           List<TopOrderProductCommand.TopOrderProductResponse> topFromDb) {
        if (topFromDb == null || topFromDb.isEmpty()) {
            String destKey = keyProvider.threeDaysZsetKey(start, end);
            stringRedisTemplate.delete(destKey);
            stringRedisTemplate.opsForZSet().add(destKey, "EMPTY", 0.0);
            setExpireIfAbsent(destKey, Duration.ofMinutes(5));
            return;
        }

        String destKey = keyProvider.threeDaysZsetKey(start, end);
        stringRedisTemplate.delete(destKey);

        // ZADD destKey score member …
        for (var row : topFromDb) {
            long productLineId = row.getProductLineId();
            double score = row.getOrderQuantity(); // DB 응답에 주문 수/합계 기준 점수
            stringRedisTemplate.opsForZSet().add(destKey, String.valueOf(productLineId), score);
        }

        // TTL (짧게)
        setExpireIfAbsent(destKey, ttlUntilMidnight());
    }

     // 4) 금일 기준 가져오기
    public List<ProductRankingDto.ProductRankEntry> todaysTopRankZset(){
        return selectZsetCache(keyProvider.realtimeKey(LocalDate.now()));
    }

    private List<ProductRankingDto.ProductRankEntry> selectZsetCache(String key){
        Set<ZSetOperations.TypedTuple<String>> tuples = stringRedisTemplate.opsForZSet()
                .rangeWithScores(key, 0, -1);

        List<ProductRankingDto.ProductRankEntry> result = new ArrayList<>();
        if (tuples != null) {
            tuples.forEach(t -> {
                Long member = Long.valueOf(t.getValue());
                int score  = t.getScore().intValue();

                result.add(new ProductRankingDto.ProductRankEntry(member, score));
            });
        }

        return result;
    }
}
