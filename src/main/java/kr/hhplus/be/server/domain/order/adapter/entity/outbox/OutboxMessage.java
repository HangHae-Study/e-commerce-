package kr.hhplus.be.server.domain.order.adapter.entity.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(
        name = "outbox_message",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_event_id", columnNames = {"event_id"}),
                @UniqueConstraint(name = "uk_order_event", columnNames = {"key_str", "topic"})
        },
        indexes = {
                @Index(name = "idx_status_id", columnList = "status,id")
        }
)
@Getter
@Setter
public class OutboxMessage {
    @Id
    @GeneratedValue(strategy=IDENTITY) Long id;
    String eventId;
    String topic;
    String keyStr;
    @Column(columnDefinition = "json") String payload; // 또는 @JdbcType(JsonType.class)

    @Enumerated(EnumType.STRING) Status status = Status.NEW;
    int retryCount;
    Instant nextAttemptAt;
    String lastError;
    Instant createdAt;
    Instant updatedAt;

    public static OutboxMessage newMessage(String eventId, String topic, String key, JsonNode payload) {
        var m = new OutboxMessage();
        m.eventId = eventId;
        m.topic = topic;
        m.keyStr = key;
        m.payload = payload.toString();
        m.status = Status.NEW;
        return m;
    }

    public void markPublished() {
        this.status = Status.PUBLISHED;
        this.lastError = null;
        this.nextAttemptAt = null;
    }

    public void markFailedWithBackoff(String error) {
        this.status = Status.NEW; // 여전히 NEW (재시도 대기)
        this.retryCount++;
        this.lastError = (error != null && error.length() > 1000) ? error.substring(0,1000) : error;
        long delayMs = Math.min(60_000, (long)Math.pow(2, Math.min(retryCount, 8)) * 200L); // 0.2s→...→~60s
        this.nextAttemptAt = Instant.now().plusMillis(delayMs);
    }

    public enum Status { NEW, PUBLISHED, FAILED }
}

