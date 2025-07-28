package kr.hhplus.be.server.domain.user.adapter.repository;

import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class UsersJpaRepositoryAdapter implements UserRepository {
    @Override
    public Optional<Users> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<Users> findAll() {
        return null;
    }

    @Override
    public Users save(Users users) {
        return null;
    }

    @Override
    public void deleteById(Long aLong) {

    }
}
