package kr.hhplus.be.server.domain.product.controller.dto;

import java.math.BigDecimal;

public record TopProductRanking(
        Long productId,
        String productName,
        BigDecimal productPrice,
        long soldCount) {}
