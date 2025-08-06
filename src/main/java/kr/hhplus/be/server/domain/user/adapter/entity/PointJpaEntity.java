package kr.hhplus.be.server.domain.user.adapter.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.dto.PointDao;
import lombok.Getter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Table(name="points")
@Getter
@Entity
public class PointJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pointId;

    private Long userId;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal balance;

    @UpdateTimestamp
    @Column(
            name = "update_dt",
            nullable = false,
            columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
    )
    private LocalDateTime updateDt;

    public PointDao toDao() {
        return PointDao.builder()
                .pointId(pointId)
                .userId(userId)
                .balance(balance)
                .updateDt(updateDt)
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
        return e;
    }
}
