package kr.hhplus.be.server.domain.coupon.adapter.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_issue")
@Getter
@NoArgsConstructor
public class CouponIssueJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponIssueId;

    private String couponCode;
    private Long couponId;
    private Long userId;
    private String couponValid;
    private BigDecimal discountRate;
    private LocalDateTime expireDate;

    @Version
    @Column(
            nullable = false,
            columnDefinition = "BIGINT NOT NULL DEFAULT 0"
    )
    private Long version;

    @UpdateTimestamp
    @Column(
            name = "update_dt",
            nullable = false,
            columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
    )
    private LocalDateTime updateDt;

    public CouponIssue toDomain() {
        return CouponIssue.builder()
                .version(version)
                .couponIssueId(this.couponIssueId)
                .couponCode(this.couponCode)
                .couponId(this.couponId)
                .userId(this.userId)
                .couponValid(this.couponValid)
                .discountRate(this.discountRate)
                .expireDate(this.expireDate)
                .updateDt(this.updateDt)
                .build();
    }

    public static CouponIssueJpaEntity fromDomain(CouponIssue issue) {
        CouponIssueJpaEntity e = new CouponIssueJpaEntity();
        if (issue.getCouponIssueId() != null) {
            e.couponIssueId = issue.getCouponIssueId();
        }
        e.couponCode    = issue.getCouponCode();
        e.couponId      = issue.getCouponId();
        e.userId        = issue.getUserId();
        e.couponValid   = issue.getCouponValid();
        e.discountRate  = issue.getDiscountRate();
        e.expireDate    = issue.getExpireDate();
        e.updateDt      = issue.getUpdateDt();
        e.version       = issue.getVersion();
        return e;
    }
}
