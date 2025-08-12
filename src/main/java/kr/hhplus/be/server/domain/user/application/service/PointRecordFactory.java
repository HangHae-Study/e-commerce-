package kr.hhplus.be.server.domain.user.application.service;

import kr.hhplus.be.server.domain.user.application.dto.PointDao;
import kr.hhplus.be.server.domain.user.application.dto.PointRecordDao;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

public class PointRecordFactory {

    public static PointRecordDao recordOfUse(PointDao point, BigDecimal amount, String reqId){
        return PointRecordDao.builder()
                .pointRecordId(null)
                .pointId(point.getPointId())
                .userId(point.getUserId())
                .requestId(reqId)
                .amount(amount)
                .type("USE")
                .build();
    }

    public static PointRecordDao recordOfCharge(PointDao point, BigDecimal amount, String reqId){
        return PointRecordDao.builder()
                .pointRecordId(null)
                .pointId(point.getPointId())
                .userId(point.getUserId())
                .requestId(reqId)
                .amount(amount)
                .type("CHARGE")
                .build();
    }

}
