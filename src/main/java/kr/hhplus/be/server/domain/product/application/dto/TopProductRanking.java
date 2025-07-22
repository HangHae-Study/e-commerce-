package kr.hhplus.be.server.domain.product.application.dto;

public record TopProductRanking(
        Long productId,
        String productName,
        double productPrice,
        long soldCount) {}
