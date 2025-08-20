package kr.hhplus.be.server.domain.product.adapter.cache;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ProductCacheKeyProvider {

    // 실시간(일자별) ZSET: zs:rank:product:realtime:YYYY-MM-DD
    private static final String REALTIME_KEY_PREFIX          = "zs:rank:product:realtime";
    // 3일 합산 ZUNION 결과 ZSET: zs:rank:product:sum:top5:3days:{start}:{end}
    private static final String FOR_3_DAYS_RANK_KEY_PREFIX   = "zs:rank:product:sum:top5:3days";
    // 3일 합산 최종 응답값(예: List<ProductLine>) 캐시: s:cache:rank:product:sum:top5:3days:{start}:{end}
    private static final String FOR_3_DAYS_CACHE_KEY_PREFIX  = "s:cache:rank:product:sum:top5:3days";

    private static final String CACHE_STEMPEED_KEY_PREFIX = "lock:s:cache:rank:product:sum:top5:3days:";
    /*
    키 빌더
     */
    String realtimeKey(LocalDate d) {
        // ex) zs:rank:product:realtime:2025-08-19
        return REALTIME_KEY_PREFIX + ":" + d;
    }

    String threeDaysZsetKey(LocalDate start, LocalDate end) {
        return FOR_3_DAYS_RANK_KEY_PREFIX + ":" + start + ":" + end;
    }

    String threeDaysValueCacheKey(LocalDate start, LocalDate end) {
        return FOR_3_DAYS_CACHE_KEY_PREFIX + ":" + start + ":" + end;
    }

    String lockCacheKey(LocalDate start, LocalDate end) {
        return CACHE_STEMPEED_KEY_PREFIX + start + ":" + end;
    }

}
