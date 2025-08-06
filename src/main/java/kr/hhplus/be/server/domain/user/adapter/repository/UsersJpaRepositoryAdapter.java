package kr.hhplus.be.server.domain.user.adapter.repository;

import kr.hhplus.be.server.domain.user.adapter.entity.UsersJpaEntity;
import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UsersJpaRepositoryAdapter implements UserRepository {

    private final UsersJpaRepository usersJpaRepository;

    @Override
    public Optional<Users> findById(Long id) {
        return usersJpaRepository.findById(id).map(UsersJpaEntity::toDomain);
    }

    @Override
    public List<Users> findAll() {
        return null;
    }

    @Override
    public Users save(Users users) {
        UsersJpaEntity updated = usersJpaRepository.save(UsersJpaEntity.fromDomain(users));
        return updated.toDomain();
    }

    @Override
    public void deleteById(Long aLong) {

    }
}
