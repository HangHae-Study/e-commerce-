package kr.hhplus.be.server.cache;

import kr.hhplus.be.server.TestDataSourceProxyConfig;
import kr.hhplus.be.server.TestcontainersConfiguration;
import kr.hhplus.be.server.domain.order.application.facade.OrderFacade;
import kr.hhplus.be.server.domain.product.adapter.cache.TopProductCacheRepository;
import kr.hhplus.be.server.domain.product.application.facade.ProductFacade;
import kr.hhplus.be.server.domain.product.command.ProductRankingDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Import({
        TestcontainersConfiguration.class,
        TestDataSourceProxyConfig.class

})
@SpringBootTest
public class TopProductRankWithRedisTest {

    @Autowired
    TopProductCacheRepository cacheRepository;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Nested
    class RedisScoringTest{
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

        @Test
        void ZSET_스코어_변경_확인(){
            List<ProductRankingDto.ProductItemForRank> orderItems = new ArrayList<>();
            int count = 10;
            for(int i = 1; i<= count; i++){
                orderItems.add(new ProductRankingDto.ProductItemForRank((long) i, i));
            }

            cacheRepository.increaseTodayRankScores(orderItems);

            List<ProductRankingDto.ProductRankEntry> result = selectZsetCache("zs:rank:product:realtime" + ":" + LocalDate.now());

            assertThat(result).isNotEmpty();
            assertThat(result).hasSize(count);

            //result.forEach(System.out::println);
        }
    }

    @Nested
    class RedisScoreWithRankTest{
        void testData(){
            LocalDate[] range = new LocalDate[]{
                    LocalDate.now().minusDays(1),
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(3),
            };

            for(int day = 0; day<3; day++){
                List<ProductRankingDto.ProductItemForRank> orderItems = new ArrayList<>();
                int count = 10;
                for(int i = 1; i<= count; i++){
                    orderItems.add(new ProductRankingDto.ProductItemForRank((long) i, i * (day+1)));
                }

                try (var mocked = Mockito.mockStatic(LocalDate.class)) {
                    mocked.when(LocalDate::now).thenReturn(range[day]);
                    cacheRepository.increaseTodayRankScores(orderItems);
                }
            }
        }

        @Test
        void 지난_3일_인기상품_랭킹_조회(){
            testData();

            List<ProductRankingDto.ProductRankEntry> rankScores = cacheRepository.topFiveForThreeDaysZset();

            assertThat(rankScores).isNotEmpty();
            assertThat(rankScores).hasSize(5);

            rankScores.forEach(System.out::println);
        }
    }

    @Autowired
    OrderFacade orderFacade;

    @Autowired
    ProductFacade productFacade;

    @Sql(scripts = {
            "classpath:sql/cleanup.sql",
            "classpath:sql/schema.sql",
            "classpath:sql/cache/august_100_data.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    class OrderRankIntegrationTest{

    }


}
