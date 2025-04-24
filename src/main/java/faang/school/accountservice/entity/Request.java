package faang.school.accountservice.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Map;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "request")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "request", cascade = CascadeType.ALL)
    private IdempotencyToken idempotencyToken;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private RequestType requestType;

    private Long lockValue;

    private boolean isOpen;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> inputData;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private String statusDetails;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    @Version
    private int version;

}
