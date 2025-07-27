package kr.hhplus.be.server.domain.user.application.facade;

import kr.hhplus.be.server.domain.user.application.Point;
import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.service.PointService;
import kr.hhplus.be.server.domain.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPointFacade {
    private final UserService userService;
    private final PointService pointService;

    public Point getPoint(Long userId){
        return pointService.getPoint(userId);
    }
    public Point chargeUserPoint(Long userId, Object amount){
        Users user = userService.getUser(userId);
        return pointService.charge(userId, amount);
    }

    public Point updateUserPoint(Long userId, Object amount){
        Users user =  userService.getUser(userId);
        return pointService.use(userId, amount);
    }


}
