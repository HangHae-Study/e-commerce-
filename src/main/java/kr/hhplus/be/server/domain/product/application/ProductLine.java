package kr.hhplus.be.server.domain.product.application;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.repository.query.ParameterOutOfBoundsException;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
public class ProductLine {
    private Long productLineId;
    private Long productId;
    private String productLineName;
    private BigDecimal productLinePrice;
    private String productLineType;
    private Long remaining;
    private LocalDateTime updateDt;

    public void decreaseStock(Long quantity) {
        if(quantity <= 0){
            throw new IllegalArgumentException("감소시킬 수량은 0보다 커야 합니다.");
        }

        if (quantity > remaining) {
            throw new IllegalArgumentException("재고가 부족합니다. (요청 수량: " + quantity + ", 남은 수량: " + remaining + ")");
        }
        remaining -= quantity;
    }

    public void increaseStock(Long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("증가시킬 수량은 0보다 커야 합니다.");
        }
        remaining += quantity;
    }
}