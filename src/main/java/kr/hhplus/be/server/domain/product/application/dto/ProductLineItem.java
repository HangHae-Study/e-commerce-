package kr.hhplus.be.server.domain.product.application.dto;

public record ProductLineItem(Long productLineId, String lineType, double linePrice, int remaining) {}
