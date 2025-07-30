package kr.hhplus.be.server.domain.product.controller.apidto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
public record ProductListResponse(
        List<ProductSummary> products
) {
    public record ProductSummary(Long productId, String name, BigDecimal price) {}

}