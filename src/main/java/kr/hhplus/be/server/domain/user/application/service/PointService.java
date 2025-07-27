package kr.hhplus.be.server.domain.user.application.service;

import kr.hhplus.be.server.domain.user.application.Point;
import kr.hhplus.be.server.domain.user.application.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;


    // 비즈니스 요구사항(유저 포인트가 존재하지 않는다면, 0원을 반환한다.)
    public Point getPoint(Long userId){
        return getOrInitialize(userId);
    }

    public Point getOrInitialize(Long userId){
        Point point = pointRepository.findByUserId(userId)
                .orElseGet(()-> {
                    Point newPoint = new Point(userId, 0);
                    pointRepository.save(newPoint);
                    return newPoint;
                }); // 유저 검증은 유저 Service에서 진행하기 때문..

        return point;
    }

    public Point charge(Long userId, Object amount){
        Point point = getPoint(userId);

        point.charge(amount);
        pointRepository.save(point);
        return point;
    }

    public Point use(Long userId, Object amount){
        Point point = getPoint(userId);
        point.use(amount);
        pointRepository.save(point);
        return point;
    }

}
