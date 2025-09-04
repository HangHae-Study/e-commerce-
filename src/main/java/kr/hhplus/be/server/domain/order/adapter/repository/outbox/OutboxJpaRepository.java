package kr.hhplus.be.server.domain.order.adapter.repository.outbox;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.order.adapter.entity.outbox.OutboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OutboxJpaRepository extends JpaRepository<OutboxMessage, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = """
      SELECT * FROM outbox_message
      WHERE status = 'NEW'
        AND (next_attempt_at IS NULL OR next_attempt_at <= CURRENT_TIMESTAMP(3))
      ORDER BY id
      LIMIT :batch
      FOR UPDATE SKIP LOCKED
      """, nativeQuery = true)
    List<OutboxMessage> lockBatchReadyToSend(@Param("batch") int batch);

    @Query("SELECT o FROM OutboxMessage o " +
            "WHERE o.status = 'NEW' AND o.createdAt <= :threshold")
    List<OutboxMessage> findStuckMessages(@Param("threshold") Instant threshold);

    Optional<OutboxMessage> findByEventId(@Param("eventId") String eventId);
}


