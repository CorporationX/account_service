package faang.school.accountservice.entity;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.TransactionType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "requests")
public class Request extends Auditing {
    @Id
    @Column(name = "idempotent_key", length = 36)
    private String idempotentKey;

    @ManyToOne
    @JoinColumn(name = "author_account_id", nullable = false, updatable = false)
    private Account author;

    @ManyToOne
    @JoinColumn(name = "receiver_account_id", nullable = false, updatable = false)
    private Account receiver;

    @Column(name = "is_lock", nullable = false)
    private boolean isLock = false;

    @Column(name = "is_open", nullable = false)
    private boolean isOpen = true;

    @Column(name = "transaction_type", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Type(JsonBinaryType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Column(name = "status", length = 32, nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(name = "status_info", length = 128)
    private String statusInfo;

    @Version
    private int version;
}
