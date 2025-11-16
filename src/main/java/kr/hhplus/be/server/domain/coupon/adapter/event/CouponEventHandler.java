package kr.hhplus.be.server.domain.coupon.adapter.event;

import kr.hhplus.be.server.domain.coupon.adapter.kafka.CouponKafkaPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponEventHandler {

    private final CouponKafkaPublisher kafkaPublisher;

    @EventListener
    public void handleCouponEnqueue(CouponIssuedEvent event){
        //kafkaPublisher.publishCouponRequest(event);
    }
}
