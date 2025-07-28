package kr.hhplus.be.server.domain.user.adapter.repository;

import kr.hhplus.be.server.domain.user.adapter.entity.UsersJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersJpaRepository extends JpaRepository<UsersJpaEntity, Long> {
}
