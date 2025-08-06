package kr.hhplus.be.server.domain.product.exception;

import lombok.Getter;

@Getter
public class OutOfStockException extends RuntimeException{
    private final Long productLineId;
    private final Long requestedQuantity;
    private final Long availableQuantity;

    public OutOfStockException(Long plId, Long reqQ, Long ablQ){
        super(String.format("재고가 부족합니다. (요청 수량: %d, 남은 수량: %d)", reqQ, ablQ));
        this.productLineId = plId;
        this.requestedQuantity = reqQ;
        this.availableQuantity = ablQ;
    }
}
