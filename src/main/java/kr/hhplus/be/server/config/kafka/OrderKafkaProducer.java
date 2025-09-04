package kr.hhplus.be.server.config.kafka;

import kr.hhplus.be.server.domain.order.adapter.entity.outbox.OutboxMessage;
import kr.hhplus.be.server.domain.order.adapter.repository.outbox.OutboxJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderKafkaProducer {
    private final OutboxJpaRepository outboxRepository;
    private final OrderKafkaPublisher kafkaPublisher;

    @Scheduled(fixedRate = 5000)
    public void outboxPublishForFail(){
        Instant threshold = Instant.now().minusSeconds(300); // 5분 전

        List<OutboxMessage> stuck = outboxRepository.findStuckMessages(threshold);
        for (OutboxMessage msg : stuck) {
            try {
                kafkaPublisher.trySendByEventId(msg.getTopic(), msg.getKeyStr(), msg.getPayload());
                msg.setStatus(OutboxMessage.Status.PUBLISHED);
            } catch (Exception ex) {
                // todo: re-tried alert for external platform
            }
        }
    }

    // 정면 네모, 위에는 동그라미, 옆에 서는 세모



}
