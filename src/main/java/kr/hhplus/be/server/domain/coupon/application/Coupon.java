package kr.hhplus.be.server.domain.coupon.application;

import kr.hhplus.be.server.domain.coupon.application.generator.CouponCodeGenerator;
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
        if(this.remaining == 0){
            throw new IllegalStateException("쿠폰 발급에 실패하였습니다.");
        }

        this.remaining --;
    }

    public CouponIssue issueTo(Long userId, CouponCodeGenerator codeGen) {
        if (!hasRemaining()) {
            throw new IllegalStateException("쿠폰 발급에 실패하였습니다.");
        }
        String code = codeGen.generate(this, userId, remaining);
        decrease();
        return CouponIssue.issueNew(this, userId, code);
    }

}
