package kr.hhplus.be.server.domain.coupon.adapter.cache;

import org.springframework.stereotype.Component;

@Component
public class CouponCacheKeyProvider {
    String q(Long couponId) { return "q:coupon:"+couponId; }                 // ZSET(대기열)
    String issued(Long couponId, Long userId) { return "s:claim:"+couponId + ":" + userId; }  // SET(발급상태 : 발급대기, 발급실패, 발급 완료)
    String stock(Long couponId) { return "stock:coupon:"+couponId; }         // STRING(잔여)
    String meta(Long couponId) { return "meta:coupon:"+couponId; }
    String issueDetail(Long couponId, Long userId){ return "s:issued:"+ couponId + ":" + userId;}
    //String lockKey(String c) { return "lock:coupon:"+c; }

    public enum CouponClaimStatus{
        ISSUED,
        FAILED,
        WAITED,
        PROCESSING,
        INIT
    }
}
