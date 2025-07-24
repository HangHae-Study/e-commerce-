package kr.hhplus.be.server.domain.product.application;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductLine {
    private final Long productLineId;
    private final Long productId;
    private final String productName;
    private final BigDecimal productLinePrice;
    private final String productLineType;
    private Long remaining;
    private final LocalDateTime updateDt;

    public void decreaseStock(Long quantity){
        remaining -= quantity;
    }

    public void increaseStock(Long quantity){
        remaining += quantity;
    }
}
