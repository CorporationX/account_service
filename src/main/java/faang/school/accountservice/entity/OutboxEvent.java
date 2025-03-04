package faang.school.accountservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "outbox_events")
public class OutboxEvent {
    @Id
    private UUID id;

    @Column(name = "class_type", nullable = false)
    private String classType;

    @Column(name = "kafka_topic", nullable = false)
    private String kafkaTopic;

    @Column(name = "json_payload", nullable = false, columnDefinition = "jsonb")
    private String jsonPayload;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public OutboxEvent(UUID id, String classType, String kafkaTopic, String jsonPayload) {
        this.id = id;
        this.classType = classType;
        this.kafkaTopic = kafkaTopic;
        this.jsonPayload = jsonPayload;
    }
}
