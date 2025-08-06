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
    private LocalDateTime udpateDt;

    public PointDao toDao() {
        return PointDao.builder()
                .pointId(pointId)
                .userId(userId)
                .balance(balance)
                .udpateDt(udpateDt)
                .build();
    }
}
