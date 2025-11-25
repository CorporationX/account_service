    package faang.school.accountservice.model;

    import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
    import jakarta.persistence.Column;
    import jakarta.persistence.Entity;
    import jakarta.persistence.EnumType;
    import jakarta.persistence.Enumerated;
    import jakarta.persistence.GeneratedValue;
    import jakarta.persistence.GenerationType;
    import jakarta.persistence.Id;
    import jakarta.persistence.Table;
    import java.time.OffsetDateTime;
    import java.util.Map;
    import java.util.UUID;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import org.hibernate.annotations.Type;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity
    @Table(name = "requests")
    public class Request {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "idempotency_key", nullable = false)
        private UUID idempotencyKey;

        @Column(name = "user_id", nullable = false)
        private long userId;

        @Enumerated(EnumType.STRING)
        @Column(name = "request_type", nullable = false, length = 32)
        private RequestType requestType;

        @Column(name = "lock_key", nullable = false, length = 64)
        private String lockKey;

        @Column(name = "is_open", nullable = false, length = 16)
        private boolean isOpen;

        @Type(JsonBinaryType.class)
        @Column(name = "input_request", nullable = false, columnDefinition = "jsonb")
        private Map<String, Object> inputRequest;

        @Enumerated(EnumType.STRING)
        @Column(name = "request_status", nullable = false, length = 32)
        private RequestStatus requestStatus;

        @Column(name = "status_details", columnDefinition = "text")
        private String statusDetails;

        @Column(name = "created_at", nullable = false)
        private OffsetDateTime createdAt;

        @Column(name = "updated_at", nullable = false)
        private OffsetDateTime updatedAt;

        @Column(name = "version", nullable = false)
        private int version = 0;

        @Column(name = "notification_pending", nullable = false)
        private boolean notificationPending;

        @Enumerated(EnumType.STRING)
        @Column(name = "pending_notification_type")
        private NotificationType pendingNotificationType;
    }