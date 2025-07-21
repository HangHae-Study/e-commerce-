package kr.hhplus.be.server.domain.user.dto;

public record BalanceChargeResponse(Long userId, java.math.BigDecimal newBalance) {}