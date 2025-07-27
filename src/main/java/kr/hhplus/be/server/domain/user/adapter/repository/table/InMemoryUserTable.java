package kr.hhplus.be.server.domain.user.adapter.repository.table;

import kr.hhplus.be.server.common.inmemory.AbstractInMemoryTable;
import kr.hhplus.be.server.domain.user.application.Users;
import org.springframework.stereotype.Component;

@Component
public class InMemoryUserTable extends AbstractInMemoryTable<Long, Users> {
}
