package kr.hhplus.be.server.domain.product.application.dto;

import java.util.List;

public record ProductDetailResponse(
        Long productId,
        String name,
        String description,
        double price,
        List<ProductLineItem> lines
) {}