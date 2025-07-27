package kr.hhplus.be.server.domain.user.adapter.repository;

import kr.hhplus.be.server.domain.user.application.Point;
import kr.hhplus.be.server.domain.user.application.repository.PointRepository;
import kr.hhplus.be.server.domain.user.adapter.repository.table.InMemoryPointTable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class InMemoryPointRepository implements PointRepository {

    private final InMemoryPointTable pointTable = new InMemoryPointTable();

    @Override
    public Optional<Point> findByUserId(Long userId) {
        return Optional.ofNullable(pointTable.findByUserId(userId));
    }

    @Override
    public Optional<Point> findById(Long id) {
        return Optional.ofNullable(pointTable.select(id));
    }

    @Override
    public List<Point> findAll() {
        return new ArrayList<>(pointTable.selectAll());
    }

    @Override
    public Point save(Point point) {
        pointTable.insert(point);
        return point;
    }

    @Override
    public void deleteById(Long id) {
        pointTable.delete(id);
    }
}

