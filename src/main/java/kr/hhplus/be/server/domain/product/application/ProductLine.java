package kr.hhplus.be.server.domain.product.application;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductLine {
    private final Long productLineId;
    private final Long productId;
    private final BigDecimal productLinePrice;
    private final String productLineType;
    private final Long remaining;
    private final LocalDateTime updateDt;
}
