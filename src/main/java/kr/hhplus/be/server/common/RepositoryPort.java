package kr.hhplus.be.server.common;

import java.util.List;
import java.util.Optional;

public interface RepositoryPort<ID, ENTITY> {

    Optional<ENTITY> findById(ID id);

    List<ENTITY> findAll();

    ENTITY save(ENTITY entity);

    void deleteById(ID id);
}


