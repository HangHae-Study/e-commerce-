package kr.hhplus.be.server.domain.product.controller.apidto;

import java.math.BigDecimal;

public record ProductSummary(Long productId, String name, BigDecimal price) {}
