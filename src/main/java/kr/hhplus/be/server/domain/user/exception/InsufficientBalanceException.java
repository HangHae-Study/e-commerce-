package kr.hhplus.be.server.domain.user.exception;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class InsufficientBalanceException extends RuntimeException{
    private final long userId;
    private final BigDecimal requiredAmount;
    private final BigDecimal currentBalance;

    public InsufficientBalanceException(long userId, BigDecimal reqAmt, BigDecimal curBal){
        super(String.format("현재 잔액이 부족합니다"));
        this.userId = userId;
        this.requiredAmount = reqAmt;
        this.currentBalance = curBal;

    }
}
