package kr.hhplus.be.server.domain.coupon.adapter.kafka;

import kr.hhplus.be.server.domain.coupon.adapter.event.CouponIssuedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponKafkaPublisher {
    private final KafkaTemplate<String, CouponIssuedEvent> kafka;
    private static final String TOPIC = "coupon.issue.v1";

    public void publishCouponRequest(CouponIssuedEvent event) {
        try{
            kafka.send(TOPIC, event.couponId().toString(), event);
        }catch(Exception e){
            //failed
        }
    }
}
