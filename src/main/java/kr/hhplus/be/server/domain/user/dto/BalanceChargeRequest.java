package kr.hhplus.be.server.domain.user.dto;

import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record BalanceChargeRequest(@Min(value=1, message = "amount must be over zero") BigDecimal amount) {}