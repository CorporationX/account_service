package faang.school.accountservice.model;

import faang.school.accountservice.enums.PaymentStages;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность для аудита изменений баланса аккаунта.
 * Хранит информацию о старом и новом значении баланса,
 * величине изменения, типе события и времени создания записи.
 */
@Entity
@Table(name = "balance_audit")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "request_id")
    private UUID requestId;

    @Column(name = "change_amount", nullable = false)
    private BigDecimal changeAmount;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Column(name = "old_balance", nullable = false)
    private BigDecimal oldBalance;

    @Column(name = "new_balance", nullable = false)
    private BigDecimal newBalance;

    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStages eventType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}