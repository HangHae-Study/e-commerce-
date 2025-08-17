package kr.hhplus.be.server.domain.product.adapter.cache;

import kr.hhplus.be.server.domain.order.command.TopOrderProductCommand;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TopProductCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    private String key(LocalDate start, LocalDate end) {
        return "top:order-products:" + start + ":" + end;
    }

    @SuppressWarnings("unchecked")
    public List<ProductLine> find(LocalDate start, LocalDate end) {
        String key = key(start, end);
        Object val = redisTemplate.opsForValue().get(key);
        if (val == null) return null;
        return (List<ProductLine>) val;
    }

    public void save(LocalDate start, LocalDate end,
                     List<ProductLine> value,
                     Duration ttl) {
        String key = key(start, end);
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    public void evict(LocalDate start, LocalDate end) {
        redisTemplate.delete(key(start, end));
    }
}
