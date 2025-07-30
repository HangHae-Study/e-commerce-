package kr.hhplus.be.server.domain.coupon.adapter.generator;

import kr.hhplus.be.server.domain.coupon.application.Coupon;
import kr.hhplus.be.server.domain.coupon.application.generator.CouponCodeGenerator;
import org.springframework.stereotype.Component;

@Component
public class DefaultCouponCodeGenerator implements CouponCodeGenerator {
    @Override
    public String generate(Coupon coupon, Long userId, Long seq) {
        return coupon.getCouponId() + "-" + userId + "-" + seq;
    }
}
