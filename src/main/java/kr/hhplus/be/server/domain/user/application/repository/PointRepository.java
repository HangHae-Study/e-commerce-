package kr.hhplus.be.server.domain.user.application.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.common.RepositoryPort;
import kr.hhplus.be.server.domain.user.application.dto.PointDao;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PointRepository  extends RepositoryPort<Long, PointDao> {

    public Optional<PointDao> findByUserId(Long userId);

    public Optional<PointDao> findByIdWithPessimisticLock(Long pId);

    public void deleteAll();
}
