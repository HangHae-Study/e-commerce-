package kr.hhplus.be.server.domain.user.adapter.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="point_records")
public class PointRecordJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long pointRecordId;

    Long pointId;
    Long userId;

    BigDecimal amount;
    String type;

    @UpdateTimestamp
    @Column(
            name = "update_dt",
            nullable = false,
            columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
    )
    private LocalDateTime udpateDt;
}
