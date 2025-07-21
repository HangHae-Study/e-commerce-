package kr.hhplus.be.server.domain.user.repository;

import kr.hhplus.be.server.common.RepositoryPort;
import kr.hhplus.be.server.domain.user.entity.Point;
import kr.hhplus.be.server.domain.user.entity.Users;

import java.util.Optional;

public interface UserRepository extends RepositoryPort<Long, Users> {
}
