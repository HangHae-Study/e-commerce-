package kr.hhplus.be.server.domain.order.application;

import kr.hhplus.be.server.domain.coupon.application.Coupon;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import kr.hhplus.be.server.domain.order.application.dto.OrderCreateRequest;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderLine {
    private Long orderLineId;
    private String orderId;

    private Long userId;
    private Long productId;
    private Long productLineId;

    private BigDecimal orderLinePrice;
    private int quantity;

    private String couponYn;
    private String couponCode;
    private BigDecimal discountPrice;

    private String status;
    private LocalDateTime orderDt;
    private LocalDateTime updateDt;

    public BigDecimal getSubtotal() {
        return orderLinePrice.multiply(new BigDecimal(quantity));
    }

    public void applyCoupon(CouponIssue coupon){
        this.couponYn = "Y";
        this.couponCode = coupon.getCouponCode();

        BigDecimal rateFraction = coupon.getDiscountRate().movePointLeft(2); // 0.2
        BigDecimal multiplier   = BigDecimal.ONE.subtract(rateFraction);

        this.discountPrice = orderLinePrice
                .multiply(multiplier)                                       // 100 * 0.8 = 80
                .setScale(0, RoundingMode.HALF_UP);
    }

    public static OrderLine create(OrderCreateRequest oReq, OrderCreateRequest.OrderItem olReq){
        OrderLine ord = OrderLine.builder()
                .orderId(oReq.orderId())
                .userId(oReq.userId())
                .productLineId(olReq.productLineId())
                .orderLinePrice(olReq.linePrice())
                .quantity(olReq.quantity())
                .orderDt(LocalDateTime.now())
                .status("O_MAKE")
                .updateDt(LocalDateTime.now())
                .build();

        if(oReq.couponCode().isBlank() || oReq.couponCode().isEmpty()){

        }else{
            ord.couponYn = "Y";
            ord.couponCode = oReq.couponCode();
        }

        return ord;
    }

    public void complete() {
        setStatus("O_CMPL");
    }
}

