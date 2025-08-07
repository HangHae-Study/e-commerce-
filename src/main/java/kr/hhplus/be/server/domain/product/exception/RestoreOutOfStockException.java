package kr.hhplus.be.server.domain.product.exception;

import kr.hhplus.be.server.domain.order.application.OrderLine;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class RestoreOutOfStockException extends RuntimeException{
    private final List<OrderLine> succeededLines;

    /**
     * @param message 오류 메시지
     * @param succeededLines 재고 차감에 성공한 OrderLine 목록
     */
    public RestoreOutOfStockException(String message, List<OrderLine> succeededLines) {
        super(message);
        this.succeededLines = Collections.unmodifiableList(succeededLines);
    }

}
