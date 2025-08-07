package kr.hhplus.be.server.domain.user.application;

public class AlreadyProcessedPointException extends RuntimeException{
    private final Long pointId;
    private final String reqId;
    private final String type;

    public AlreadyProcessedPointException(Long pointId, String reqId, String type) {
        super("이미 처리된 포인트 요청입니다");
        this.pointId = pointId;
        this.reqId = reqId;
        this.type = type;
    }
}
