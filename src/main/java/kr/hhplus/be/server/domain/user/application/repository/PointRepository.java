package kr.hhplus.be.server.domain.user.application.repository;

import kr.hhplus.be.server.common.RepositoryPort;
import kr.hhplus.be.server.domain.user.application.Point;

import java.util.Optional;

public interface PointRepository  extends RepositoryPort<Long, Point> {

    public Optional<Point> findByUserId(Long userId);

}
