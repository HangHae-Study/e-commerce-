package kr.hhplus.be.server.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import org.springframework.data.repository.query.ParameterOutOfBoundsException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Point {
    private Long pointId;
    private Long userId;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal balance;

    private LocalDateTime updateDt;

    //Domain
    public Point charge(Object amount){
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


    public Point(Long uId){
        this.userId = uId;
        this.balance = BigDecimal.ZERO;
        updateDt = LocalDateTime.now();

    }

    public Point(Long uId, Object balance){
        this(uId);
        this.balance = new BigDecimal(balance.toString());
    }

    public Point(Long pId, Long uId, Object balance){
        this(uId);
        this.pointId = pId;
        this.balance = new BigDecimal(balance.toString());
    }
}
