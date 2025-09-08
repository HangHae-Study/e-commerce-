package kr.hhplus.be.server.domain.coupon.adapter.event;

import java.time.Instant;
import java.util.UUID;

public record CouponIssuedEvent(
        String eventId,
        Long couponId,
        Long userId,
        Instant requestedAt
) {
    public static CouponIssuedEvent of(Long couponId, Long userId) {
        return new CouponIssuedEvent(
                UUID.randomUUID().toString(),
                couponId,
                userId,
                Instant.now()
        );
    }
}