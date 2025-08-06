package kr.hhplus.be.server.domain.product.controller.apidto;

public record TopProductRanking(
        Long productId,
        String productName,
        double productPrice,
        long soldCount) {}
