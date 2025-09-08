package kr.hhplus.be.server.domain.order.adapter.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderKafkaPublisher {
    private final KafkaTemplate<String, Object> kafka;
    private static final String topic = "order.events.v1";
    public void trySendByEventId(String eventId, String keyStr, Object payload){
        try{
            kafka.send(topic, keyStr, payload).get();
        }catch(Exception e){
            // failed
        }
    }
}
