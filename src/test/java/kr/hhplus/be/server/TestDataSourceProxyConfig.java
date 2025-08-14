package kr.hhplus.be.server;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@Import(TestcontainersConfiguration.class) // 질문에서 준 컨테이너 설정을 그대로 사용
public class TestDataSourceProxyConfig {

    @Bean(name = "dataSource")
    public DataSource originalDataSource() {
        // TestcontainersConfiguration 에서 이미 start() + System.setProperty 완료됨
        // 그래도 안전하게 System props에서 읽어 생성
        String url = System.getProperty("spring.datasource.url");
        String user = System.getProperty("spring.datasource.username");
        String pass = System.getProperty("spring.datasource.password");

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setUsername(user);
        cfg.setPassword(pass);
        // 옵션: 드라이버/풀 옵션들
        cfg.setMaximumPoolSize(10);
        cfg.setMinimumIdle(1);
        cfg.setPoolName("TEST-HIKARI");

        return new HikariDataSource(cfg);
    }

    @Bean
    @Primary
    public DataSource proxiedDataSource(@Qualifier("dataSource") DataSource real) {
        return ProxyDataSourceBuilder
                .create(real)
                .name("TEST-DS")
                .countQuery()           // QueryCountHolder 활성화
                // .multiline()         // (옵션) SQL 포맷 예쁘게
                // .logQueryBySlf4j()   // (옵션) SLF4J 로깅
                .build();
    }
}