package kr.hhplus.be.server.domain.product.controller.dto;

import java.util.List;

public record TopProductsResponse(
        List<TopProductRanking> topProducts
) {}