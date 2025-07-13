package faang.school.accountservice.entity;

import faang.school.accountservice.enums.Category;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.Initiator;
import faang.school.accountservice.enums.TransferStage;
import faang.school.accountservice.enums.TransferStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "balance_transfer")
public class BalanceTransfer {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "authorization_id", nullable = false)
    private UUID authorizationId;

    @ManyToOne
    @JoinColumn(name = "source_account_id", nullable = false)
    private Account sourceAccount;

    @ManyToOne
    @JoinColumn(name = "target_account_id", nullable = false)
    private Account targetAccount;

    @Column(name = "amount", precision = 30, scale = 10, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Builder.Default
    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category = Category.NO_CATEGORY;

    @Builder.Default
    @Column(name = "transfer_stage", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransferStage transferStage = TransferStage.PENDING;

    @Column(name = "clearing_initiator")
    @Enumerated(EnumType.STRING)
    private Initiator initiator;

    @Builder.Default
    @Column(name = "transfer_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransferStatus transferStatus = TransferStatus.IN_PROCESS;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(nullable = false)
    private Integer version;
}