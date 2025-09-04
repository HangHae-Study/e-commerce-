package kr.hhplus.be.server.config.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.order.adapter.entity.outbox.OutboxMessage;
import kr.hhplus.be.server.domain.order.adapter.event.OrderCompletedEvent;
import kr.hhplus.be.server.domain.order.adapter.repository.outbox.OutboxJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaConsumer {
    private final ObjectMapper om;
    private final OutboxJpaRepository outboxRepository;

    @KafkaListener(topics = "order.events.v1", groupId = "order-service")
    @Transactional
    public void onOrderComplete(JsonNode o, Acknowledgment ack){
        try{
            OrderCompletedEvent event = om.treeToValue(o, OrderCompletedEvent.class);

            OutboxMessage outboxMsg =  outboxRepository.findByEventId(event.eventId())
                    .orElseThrow();

            outboxMsg.markPublished();

            ack.acknowledge();
        }catch(Exception e){

        }
    }
}
