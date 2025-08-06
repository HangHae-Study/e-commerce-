package kr.hhplus.be.server.domain.coupon.application.generator;

import kr.hhplus.be.server.domain.coupon.application.Coupon;

public interface CouponCodeGenerator {
    /**
     * @param coupon   발급 대상 쿠폰
     * @param userId   발급 대상 사용자
     * @param sequence 남은 개수(또는 원하는 시드)
     */
    String generate(Coupon coupon, Long userId, Long sequence);


}
