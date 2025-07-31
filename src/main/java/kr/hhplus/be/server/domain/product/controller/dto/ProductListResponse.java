package kr.hhplus.be.server.domain.product.controller.dto;

import java.math.BigDecimal;
import java.util.List;
public record ProductListResponse(
        List<ProductSummary> products
) {
    public record ProductSummary(Long productId, String name, BigDecimal price) {}

}