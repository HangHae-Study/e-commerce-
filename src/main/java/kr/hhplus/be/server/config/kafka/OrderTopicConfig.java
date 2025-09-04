package kr.hhplus.be.server.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class OrderTopicConfig {

    @Bean
    NewTopic orderEvents(){
        return TopicBuilder.name("order.events.v1")
                .partitions(6).replicas(1)
                .config("cleanup.policy", "delete")
                .config("retention.ms", "300000") // 5분
                .build();
    }
}
