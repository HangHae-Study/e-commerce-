package kr.hhplus.be.server.domain.order.application.exception;

public class AlreadyProcessedOrderException extends RuntimeException{
    private String orderCode;
    private Long orderId;

    public AlreadyProcessedOrderException(Long id, String code){
        super("주문이 이미 처리된 상태 입니다.");
        this.orderId = id;
        this.orderCode = code;
    }
}
