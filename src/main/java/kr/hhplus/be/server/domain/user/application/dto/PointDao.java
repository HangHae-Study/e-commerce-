package kr.hhplus.be.server.domain.user.application.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PointDao {
    private Long pointId;
    private Long userId;
    private BigDecimal balance;
    private LocalDateTime updateDt;
}
