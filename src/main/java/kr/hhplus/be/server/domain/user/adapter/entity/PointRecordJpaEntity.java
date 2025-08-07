package kr.hhplus.be.server.domain.user.adapter.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.order.adapter.entity.OrderJpaEntity;
import kr.hhplus.be.server.domain.user.application.dto.PointRecordDao;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="point_records")
public class PointRecordJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long pointRecordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id")
    private PointJpaEntity point;
    Long userId;

    @Column(
            name = "request_id",
            nullable = false,
            length = 100,
            unique = true
    )
    private String requestId;     // ← Idempotent Key

    BigDecimal amount;

    @Column(length = 10)
    String type;

    @UpdateTimestamp
    @Column(
            name = "update_dt",
            nullable = false,
            columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
    )
    private LocalDateTime updateDt;

    public PointRecordDao toDao() {
        return PointRecordDao.builder()
                .pointRecordId(pointRecordId)
                .pointId(point.getPointId())
                .userId(userId)
                .requestId(requestId)
                .amount(amount)
                .type(type)
                .updateDt(updateDt)
                .build();
    }

    public static PointRecordJpaEntity fromDao(PointRecordDao d, PointJpaEntity pointEntity) {
        PointRecordJpaEntity e = new PointRecordJpaEntity();
        if (d.getPointRecordId() != null) e.pointRecordId = d.getPointRecordId();
        e.point      = pointEntity;
        e.userId     = d.getUserId();
        e.requestId  = d.getRequestId();
        e.amount     = d.getAmount();
        e.type       = d.getType();

        return e;
    }
}
