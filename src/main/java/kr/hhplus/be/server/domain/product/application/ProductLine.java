package kr.hhplus.be.server.domain.product.application;

import kr.hhplus.be.server.domain.product.exception.OutOfStockException;
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
            throw new OutOfStockException(productLineId, quantity, remaining);
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