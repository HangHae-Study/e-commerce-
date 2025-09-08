package kr.hhplus.be.server.domain.order.adapter.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.order.adapter.kafka.OrderKafkaPublisher;
import kr.hhplus.be.server.domain.order.adapter.entity.outbox.OutboxMessage;
import kr.hhplus.be.server.domain.order.adapter.repository.outbox.OutboxJpaRepository;
import kr.hhplus.be.server.domain.product.adapter.cache.TopProductCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderEventHandler {
    private final TopProductCacheRepository productCacheRepo;
    private final OutboxJpaRepository outboxRepository;
    private final OrderKafkaPublisher orderKafkaPublisher;

    private final ObjectMapper om;
    //private final ExternalApiClient apiClient;


    // ranking to redis
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCompleted(OrderCompletedEvent event){
        productCacheRepo.increaseTodayRankScores(
                event.orderDate(),
                event.items()
        );
    }


    // kafka
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void publishToKafka(OrderCompletedEvent event){
        orderKafkaPublisher.trySendByEventId(
                event.eventId(),
                event.orderCode(),
                om.valueToTree(event)
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveOutBox(OrderCompletedEvent event){
        outboxRepository.save(
                OutboxMessage.newMessage(
                        event.eventId(),
                        "order.events.v1",
                        String.valueOf(event.orderId()),
                        om.valueToTree(event)
                )
        );
    }

}
