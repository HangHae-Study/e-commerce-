package kr.hhplus.be.server.domain.user.controller.dto;

public record BalanceChargeResponse(Long userId, java.math.BigDecimal newBalance) {}