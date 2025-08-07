package kr.hhplus.be.server.domain.user.application.repository;

import kr.hhplus.be.server.common.RepositoryPort;
import kr.hhplus.be.server.domain.user.application.Users;

public interface UserRepository extends RepositoryPort<Long, Users> {
    public void deleteAll();
}
