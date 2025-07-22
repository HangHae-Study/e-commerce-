package kr.hhplus.be.server.domain.product.application.dto;

import java.util.List;

public record ProductListResponse(
        List<ProductSummary> products
) {}