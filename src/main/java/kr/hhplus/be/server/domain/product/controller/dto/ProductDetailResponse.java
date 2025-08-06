package kr.hhplus.be.server.domain.product.controller.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailResponse(
        Long productId,
        String name,
        String description,
        BigDecimal price,
        List<ProductLineItem> lines
) {
    public record ProductLineItem(Long productLineId, String lineType, BigDecimal linePrice, Long remaining) {}

}