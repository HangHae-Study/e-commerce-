package kr.hhplus.be.server.domain.coupon.adapter.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.coupon.application.Coupon;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon")
@Getter
@NoArgsConstructor
public class CouponJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;
    private Long totalIssued;
    private Long remaining;
    private BigDecimal discountRate;
    private LocalDateTime expireDate;
    private LocalDateTime updateDt;

    public Coupon toDomain() {
        return Coupon.builder()
                .couponId(this.couponId)
                .totalIssued(this.totalIssued)
                .remaining(this.remaining)
                .discountRate(this.discountRate)
                .expireDate(this.expireDate)
                .updateDt(this.updateDt)
                .build();
    }

    public static CouponJpaEntity fromDomain(Coupon coupon) {
        CouponJpaEntity e = new CouponJpaEntity();
        if (coupon.getCouponId() != null) {
            e.couponId = coupon.getCouponId();
        }
        e.totalIssued    = coupon.getTotalIssued();
        e.remaining      = coupon.getRemaining();
        e.discountRate   = coupon.getDiscountRate();
        e.expireDate     = coupon.getExpireDate();
        e.updateDt       = coupon.getUpdateDt();
        return e;
    }
}
