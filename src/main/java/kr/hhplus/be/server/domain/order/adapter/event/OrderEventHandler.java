package kr.hhplus.be.server.domain.order.adapter.event;

import kr.hhplus.be.server.domain.product.adapter.cache.TopProductCacheRepository;
import kr.hhplus.be.server.domain.product.command.ProductRankingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderEventHandler {
    private final TopProductCacheRepository productCacheRepo;
    //private final ExternalApiClient apiClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCompleted(OrderCompletedEvent event){
        productCacheRepo.increaseTodayRankScores(
                event.orderDate(),
                event.items()
        );
    }

}
