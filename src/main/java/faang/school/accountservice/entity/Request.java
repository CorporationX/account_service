package faang.school.accountservice.entity;

import faang.school.accountservice.converter.JsonConverter;
import faang.school.accountservice.enums.OperationType;
import faang.school.accountservice.enums.RequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "request")
public class Request {

    @Id
    @Column(name = "idempotency_token", nullable = false)
    private UUID idempotencyToken;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "operation_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private OperationType operationType;

    @Column(name = "lock_value", nullable = false)
    private Long lockValue;

    @Column(name = "is_open", nullable = false)
    private boolean isOpen;

    @Column(name = "input_data")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> inputData;

    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    @Column(name = "status_details")
    private String statusDetails;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "version", nullable = false)
    @Version
    private Long version;
}
