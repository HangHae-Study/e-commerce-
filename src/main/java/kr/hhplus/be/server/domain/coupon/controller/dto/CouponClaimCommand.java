package kr.hhplus.be.server.domain.coupon.controller.dto;

import kr.hhplus.be.server.domain.coupon.adapter.cache.CouponCacheKeyProvider;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;

public class CouponClaimCommand {
    public record CouponClaimRequest(Long couponId, Long userId) {}
    public record CouponClaimResponse(
            Long couponId,
            Long userId,

            CouponCacheKeyProvider.CouponClaimStatus status,
            Long rank,

            String couponCode,
            String issueDt,
            String expireDt
    ) { }

    public static CouponClaimResponse issued(CouponIssue iss){
        return new CouponClaimResponse(
                iss.getCouponId(),
                iss.getUserId(),
                CouponCacheKeyProvider.CouponClaimStatus.ISSUED,
                0L,

                iss.getCouponCode(),
                iss.getUpdateDt().toString(),
                iss.getExpireDate().toString()
        );
    }

    public static CouponClaimResponse ranked(Long couponId, Long userId, Long r){
        return new CouponClaimResponse(
                couponId,
                userId,
                CouponCacheKeyProvider.CouponClaimStatus.WAITED,
                r,

                "",
                "",
                ""
        );
    }

    public static CouponClaimResponse init(Long couponId, Long userId){
        return new CouponClaimResponse(
                couponId,
                userId,
                CouponCacheKeyProvider.CouponClaimStatus.INIT,
                0L,

                "",
                "",
                ""
        );
    }


}
