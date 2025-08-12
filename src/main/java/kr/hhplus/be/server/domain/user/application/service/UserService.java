package kr.hhplus.be.server.domain.user.application.service;

import kr.hhplus.be.server.domain.user.application.AlreadyProcessedPointException;
import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.dto.PointDao;
import kr.hhplus.be.server.domain.user.application.dto.PointRecordDao;
import kr.hhplus.be.server.domain.user.application.repository.PointRecordRepository;
import kr.hhplus.be.server.domain.user.application.repository.PointRepository;
import kr.hhplus.be.server.domain.user.application.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import static kr.hhplus.be.server.domain.user.application.service.PointRecordFactory.recordOfCharge;
import static kr.hhplus.be.server.domain.user.application.service.PointRecordFactory.recordOfUse;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final PointRecordRepository pointRecordRepository;

    @Transactional
    public Users getUser(Long userId){
         Users user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저 ID입니다."));

         user.setBalance(getPoint(userId).getBalance());

         return user;
    }

    @Transactional
    public PointDao getPoint(Long userId){
        PointDao pointDao = //pointRepository.findByUserId(userId)
                pointRepository.findByIdWithPessimisticLock(userId)
                .orElseGet(() -> {
                    PointDao newP = PointDao.builder()
                            .userId(userId)
                            .balance(BigDecimal.ZERO)
                            .pointRecords(new ArrayList<>())
                            .build();

                    pointRepository.save(newP);
                    return newP;
                });
        return pointDao;
    }

    @Transactional
    public Users chargePoint(Long userId, Object amount){
        PointDao point = getPoint(userId);
        // more : 포인트 레포 적용 코드

        Users user = getUser(userId);
        user.pointCharge(amount);
        userRepository.save(user);

        point.setBalance(user.getBalance());
        pointRepository.save(point);

        return user;
    }

    @Transactional
    public Users chargePointWithLock(Long userId, Object amount, String reqId){
        PointDao point = getPoint(userId);
        PointRecordDao records = recordOfCharge(point, new BigDecimal(amount.toString()), reqId);
        Users user = getUser(userId);
        try{
            user.pointCharge(amount);
            userRepository.save(user);

            point.setBalance(user.getBalance());
            pointRepository.save(point);
            pointRecordRepository.save(records);
            return user;
        }catch(DataIntegrityViolationException ex){
            throw new AlreadyProcessedPointException(point.getPointId(), "CHARGE", reqId);
        }
    }

    @Transactional
    public Users usePoint(Long userId, Object amount){
        PointDao point = getPoint(userId);

        Users user = getUser(userId);
        user.pointUse(amount);
        userRepository.save(user);

        point.setBalance(user.getBalance());
        pointRepository.save(point);
        return user;
    }

    @Transactional
    public Users payPointWithLock(Long userId, Object amount, String reqId){
        PointDao point = getPoint(userId);
        PointRecordDao records = recordOfUse(point, new BigDecimal(amount.toString()), reqId);
        Users user = getUser(userId);

        try{
            user.pointUse(amount);
            userRepository.save(user);

            point.setBalance(user.getBalance());
            pointRepository.save(point);
            pointRecordRepository.save(records);
            return user;
        }catch(DataIntegrityViolationException ex){
            throw new AlreadyProcessedPointException(point.getPointId(), "USE", reqId);
        }

    }

}
