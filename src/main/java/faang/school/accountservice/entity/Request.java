package faang.school.accountservice.entity;

import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "request")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type", nullable = false)
    private RequestType type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "input_data", columnDefinition = "jsonb")
    private Map<String, Object> inputData;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    @Column(name = "status_description", length = 512)
    private String statusDescription;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "context", columnDefinition = "text")
    private String context;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RequestTask> requestTasks;
}