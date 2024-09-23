package faang.school.accountservice.entity;

import faang.school.accountservice.enums.OperationType;
import faang.school.accountservice.enums.StatusType;
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

@Entity
@Table(name = "requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id; //если такой уже есть, то проверяем его статус(если статус в процессе и флаг открыт
    // , то можно
    // внести корректировку по значению мапы

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "operation_type")
    @Enumerated(EnumType.STRING)
    private OperationType operationType;

    @Column(name = "lock_request")
    private boolean lockRequest;

    @Column(name = "lock_user")
    private Long lockUser;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSONB", name = "request_data")
    private Map<String, Object> requestData;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusType statusType;

    @Column(name = "description")
    private String description;

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
    private long version;
}
