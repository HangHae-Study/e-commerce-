package kr.hhplus.be.server.domain.user.application;

import kr.hhplus.be.server.domain.user.exception.InsufficientBalanceException;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class Users {
    private Long userId;
    private String username;

    private BigDecimal balance;

    private LocalDateTime createDt;
    private LocalDateTime updateDt;

    public Users pointCharge(Object amount){
        if(!(amount instanceof Number)){
            throw new NumberFormatException("포인트 충전량은 숫자로 표기하여야합니다.");
        }

        BigDecimal amt = new BigDecimal(amount.toString());

        if(amt.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("포인트 충전량은 0보다 커야 합니다.");
        }

        this.balance = this.balance.add(amt);

        return this;
    }

    public Users pointUse(Object amount){
        if(!(amount instanceof Number)){
            throw new NumberFormatException("포인트 사용량은 숫자로 표기하여야합니다.");
        }
        BigDecimal amt = new BigDecimal(amount.toString());

        if(amt.compareTo(balance) > 0){
            throw new InsufficientBalanceException(userId, balance, amt);
        }

        this.balance = this.balance.subtract(amt);

        return this;
    }

}
