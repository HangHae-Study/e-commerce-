package kr.hhplus.be.server.domain.user.application.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PointRecordDao {
    private final Long        pointRecordId;
    private final Long        pointId;
    private final Long        userId;
    private final String      requestId;
    private final BigDecimal  amount;
    private final String      type;
    private final LocalDateTime updateDt;
}