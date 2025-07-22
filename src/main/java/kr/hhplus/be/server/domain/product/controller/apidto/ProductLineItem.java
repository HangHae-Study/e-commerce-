package kr.hhplus.be.server.domain.product.controller.apidto;

import java.math.BigDecimal;

public record ProductLineItem(Long productLineId, String lineType, BigDecimal linePrice, Long remaining) {}
