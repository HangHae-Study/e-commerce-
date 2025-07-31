package kr.hhplus.be.server.domain.product.controller.dto;

public record TopProductRanking(
        Long productId,
        String productName,
        double productPrice,
        long soldCount) {}
