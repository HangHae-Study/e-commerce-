package kr.hhplus.be.server.domain.product.controller.apidto;

import java.util.List;

public record ProductListResponse(
        List<ProductSummary> products
) {}