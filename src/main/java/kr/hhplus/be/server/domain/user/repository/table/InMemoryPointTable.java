package kr.hhplus.be.server.domain.user.repository.table;

import kr.hhplus.be.server.common.inmemory.AbstractInMemoryTable;
import kr.hhplus.be.server.domain.user.entity.Point;
import org.springframework.stereotype.Component;

@Component
public class InMemoryPointTable extends AbstractInMemoryTable<Long, Point> {

    private Long seq = 1L;

    public void insert(Point value){
        Long pId = value.getPointId() == null ? -1 : value.getPointId();
        Point point = store.getOrDefault(pId, new Point(seq++, value.getUserId(), value.getBalance()));
        store.put(point.getPointId(), point);
    }

    public Point findByUserId(Long userId) {
        return store.values().stream()
                .filter(point -> point.getUserId().equals(userId))
                .findFirst()
                .orElse(null); // 또는 Optional<Point>로 반환해도 좋습니다
    }
}
