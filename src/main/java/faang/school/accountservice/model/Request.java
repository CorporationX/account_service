package faang.school.accountservice.model;

import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "request")
public class Request {
    @Id
    @Column(name = "idempotency_token", nullable = false, updatable = false)
    private UUID idempotencyToken;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "request_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestType requestType;

    @Column(name = "value_lock")
    private String valueLock;

    @Column(name = "is_open", nullable = false)
    private Boolean isOpen;

    @Column(name = "request_input_data")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> requestInputData;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(name = "status_description")
    private String statusDescription;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}

