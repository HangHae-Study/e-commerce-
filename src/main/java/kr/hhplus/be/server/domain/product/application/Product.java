package kr.hhplus.be.server.domain.product.application;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private final Long productId;
    private final String productName;
    private final BigDecimal productPrice;
    private final LocalDateTime updateDt;
}
