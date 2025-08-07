package kr.hhplus.be.server.domain.user.adapter.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.order.adapter.entity.OrderLineJpaEntity;
import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.dto.PointDao;
import kr.hhplus.be.server.domain.user.application.dto.PointRecordDao;
import lombok.Getter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Table(name="points")
@Getter
@Entity
public class PointJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pointId;

    private Long userId;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal balance;

    @OneToMany(mappedBy = "point", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PointRecordJpaEntity> pointRecords = new ArrayList<>();

    @UpdateTimestamp
    @Column(
            name = "update_dt",
            nullable = false,
            columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
    )
    private LocalDateTime updateDt;

    public PointDao toDao() {
        List<PointRecordDao> records = pointRecords.stream().map(PointRecordJpaEntity::toDao).collect(Collectors.toList());
        return PointDao.builder()
                .pointId(pointId)
                .userId(userId)
                .balance(balance)
                .updateDt(updateDt)
                .pointRecords(records)
                .build();
    }

    // Domain → JPA Entity
    public static PointJpaEntity fromDomain(PointDao domain) {
        PointJpaEntity e = new PointJpaEntity();
        // ID는 신규 생성 시엔 null, 수정 시엔 domain.getPointId()가 채워져 있어야 합니다.
        e.pointId = domain.getPointId();
        e.userId  = domain.getUserId();
        e.balance = domain.getBalance();
        e.updateDt = domain.getUpdateDt();
        e.pointRecords = domain.getPointRecords().stream().map(
                record -> PointRecordJpaEntity.fromDao(record, e)
        ).toList();
        return e;
    }
}
