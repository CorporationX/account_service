package faang.school.accountservice.entity;

import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestType;
import faang.school.accountservice.enums.RequestVersion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "request_event")
public class RequestEvent {

    @Id
    @org.hibernate.validator.constraints.UUID
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "request_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestType requestType;

    @Column(name = "block_value", nullable = false, unique = true)
    private long blockValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> body = new HashMap<>();

    @Column(name = "request_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    @Column(name = "details")
    private String details;

    @Column(name = "request_version", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private RequestVersion requestVersion;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
