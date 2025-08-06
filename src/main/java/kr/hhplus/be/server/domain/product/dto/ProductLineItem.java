package kr.hhplus.be.server.domain.product.dto;

public record ProductLineItem(Long productLineId, String lineType, double linePrice, int remaining) {}
