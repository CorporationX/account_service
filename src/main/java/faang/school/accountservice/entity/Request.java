package faang.school.accountservice.entity;

import faang.school.accountservice.enums.RequestType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "request")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    @Id
    @Column(name = "idempotent_tocken", nullable = false, unique = true)
    private UUID idempotentToken;

    @Column(name = "request_type", nullable = false)
    private RequestType requestType;

    @Column(name = "context", length = 256)
    private String context;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;
}
