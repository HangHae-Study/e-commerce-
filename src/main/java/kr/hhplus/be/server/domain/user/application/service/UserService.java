package kr.hhplus.be.server.domain.user.application.service;

import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.dto.PointDao;
import kr.hhplus.be.server.domain.user.application.repository.PointRepository;
import kr.hhplus.be.server.domain.user.application.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PointRepository pointRepository;

    public Users getUser(Long userId){
         Users user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저 ID입니다."));

         user.setBalance(getPoint(userId).getBalance());

         return user;
    }

    public PointDao getPoint(Long userId){
        PointDao pointDao = pointRepository.findByUserId(userId)
                .orElseGet(() -> {
                    PointDao newP = PointDao.builder()
                            .userId(userId)
                            .balance(BigDecimal.ZERO)
                            .build();

                    pointRepository.save(newP);
                    return newP;
                });
        return pointDao;
    }

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
    public Users usePoint(Long userId, Object amount){
        PointDao point = getPoint(userId);
        // more : 포인트 레포 적용 코드

        Users user = getUser(userId);
        user.pointUse(amount);
        userRepository.save(user);

        point.setBalance(user.getBalance());
        pointRepository.save(point);

        return user;
    }
}
