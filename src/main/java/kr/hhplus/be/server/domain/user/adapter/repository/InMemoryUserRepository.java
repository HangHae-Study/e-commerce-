package kr.hhplus.be.server.domain.user.adapter.repository;

import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.repository.UserRepository;
import kr.hhplus.be.server.domain.user.adapter.repository.table.InMemoryUserTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {
    private final InMemoryUserTable userTable = new InMemoryUserTable();

    @Override
    public Optional<Users> findById(Long userId) {
        return Optional.ofNullable(userTable.select(userId));
    }

    @Override
    public List<Users> findAll() {
        return null;
    }

    @Override
    public Users save(Users users) {
        userTable.insert(users.getUserId(), users);
        return users;
    }

    @Override
    public void deleteById(Long aLong) {

    }
}
