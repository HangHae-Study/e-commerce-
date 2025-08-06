package kr.hhplus.be.server.domain.coupon.application;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class Coupon {

    private Long couponId;
    private Long totalIssued;
    private Long remaining;
    private BigDecimal discountRate;
    private LocalDateTime expireDate;
    private LocalDateTime updateDt;

    public boolean hasRemaining(){
        return remaining > 0;
    }

    public void decrease(){
        this.remaining --;
    }

}
