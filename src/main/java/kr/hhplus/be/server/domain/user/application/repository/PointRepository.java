package kr.hhplus.be.server.domain.user.application.repository;

import kr.hhplus.be.server.common.RepositoryPort;
import kr.hhplus.be.server.domain.user.application.dto.PointDao;

import java.util.Optional;

public interface PointRepository  extends RepositoryPort<Long, PointDao> {

    public Optional<PointDao> findByUserId(Long userId);

}
