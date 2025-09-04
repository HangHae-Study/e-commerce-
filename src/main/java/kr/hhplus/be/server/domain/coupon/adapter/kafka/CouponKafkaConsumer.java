package kr.hhplus.be.server.domain.coupon.adapter.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.coupon.adapter.event.CouponIssuedEvent;
import kr.hhplus.be.server.domain.coupon.adapter.worker.CouponIssueWorker;
import kr.hhplus.be.server.domain.coupon.application.service.CouponService;
import kr.hhplus.be.server.domain.order.adapter.entity.outbox.OutboxMessage;
import kr.hhplus.be.server.domain.order.adapter.event.OrderCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponKafkaConsumer {
    private final ObjectMapper om;
    private final CouponIssueWorker couponIssueWorker;

    private final CouponService service;

    @KafkaListener(
            topics = "coupon.issue.v1",
            groupId = "coupon-service" ,
            containerFactory = "couponKafkaListenerFactory"
    )
    @Transactional
    public void onCouponEnqueued(CouponIssuedEvent event, Acknowledgment ack){
        try{
            // 레디스 컨슈밍 및 캐시
            couponIssueWorker.processCouponIssue(event.userId(), event.couponId());

            // 쿠폰 저장
            service.newCouponIssueWithOutDistLock(event.userId(), event.couponId());

            ack.acknowledge();
        }catch(Exception e){
            System.out.println("컨슈머 에러: " + e);
        }
    }
}
