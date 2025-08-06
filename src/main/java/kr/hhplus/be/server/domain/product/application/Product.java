package kr.hhplus.be.server.domain.product.application;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class Product {
    private Long productId;
    private String productName;
    private List<ProductLine> productLines;
    private BigDecimal productPrice;
    private LocalDateTime updateDt;

}
