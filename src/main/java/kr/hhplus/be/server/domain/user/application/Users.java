package kr.hhplus.be.server.domain.user.application;

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

    public void pointUse(Object amount){
        BigDecimal amt = new BigDecimal(amount.toString());
        this.balance = this.balance.subtract(amt);
    }

}
