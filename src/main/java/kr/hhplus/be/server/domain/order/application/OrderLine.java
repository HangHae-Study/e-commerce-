package kr.hhplus.be.server.domain.order.application;

import kr.hhplus.be.server.common.optimistic.VersionedDomain;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import kr.hhplus.be.server.domain.order.controller.dto.OrderCreateRequest;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Data
@SuperBuilder
public class OrderLine extends VersionedDomain {
    private Long orderLineId;
    private final Long orderId;

    private final Long userId;
    private final Long productId;
    private final Long productLineId;

    private final BigDecimal orderLinePrice;
    private final int quantity;

    private String couponYn;
    private String couponCode;
    private BigDecimal discountPrice;

    private String status;
    private LocalDateTime orderDt;
    private LocalDateTime updateDt;

    public void complete() {
        if (!"O_MAKE".equals(status)) throw new IllegalStateException("주문 완료할 수 없는 상태입니다.");

        setStatus("O_CMPL");
        setUpdateDt(LocalDateTime.now());
    }

    public void fail() {
        if (!"O_MAKE".equals(status)) throw new IllegalStateException("주문 이미 처리된 상태입니다.");

        setStatus("O_FAIL");
        setUpdateDt(LocalDateTime.now());
    }

    public BigDecimal getSubtotal() {
        return orderLinePrice.multiply(new BigDecimal(quantity));
    }

    public void applyCoupon(BigDecimal discountRate){
        this.couponYn = "Y";

        // 할인율을 0.2로 변환
        BigDecimal rateFraction = discountRate.movePointLeft(2);
        BigDecimal multiplier   = BigDecimal.ONE.subtract(rateFraction);

        this.discountPrice = orderLinePrice
                .multiply(multiplier)                                       // 100 * 0.8 = 80
                .setScale(0, RoundingMode.HALF_UP);
    }

}

