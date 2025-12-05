package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.AccountOperationException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "account", schema = "public", indexes = {
        @Index(name = "idx_account_owner", columnList = "owner_id,owner_type"),
        @Index(name = "idx_account_status", columnList = "status"),
        @Index(name = "idx_account_currency", columnList = "currency"),
        @Index(name = "idx_account_number", columnList = "number")
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number", nullable = false, unique = true, length = 20)
    @NotBlank(message = "Account number is required")
    @Pattern(regexp = "^[0-9]{12,20}$", message = "Account number must be 12-20 digits")
    private String number;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false, length = 20)
    private OwnerType ownerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private AccountType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 3)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Version
    @Column(name = "version", nullable = false)
    @Builder.Default
    private Long version = 0L;

    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    public boolean isFrozen() {
        return status == AccountStatus.FROZEN;
    }

    public boolean isClosed() {
        return status == AccountStatus.CLOSED;
    }

    public void block() {
        if (isClosed()) {
            throw new AccountOperationException("Cannot block closed account");
        }
        if (isFrozen()) {
            throw new AccountOperationException("Account is already frozen");
        }
        this.status = AccountStatus.FROZEN;
    }

    public void unblock() {
        if (isClosed()) {
            throw new AccountOperationException("Cannot unblock closed account");
        }
        if (isActive()) {
            throw new AccountOperationException("Account is already active");
        }
        this.status = AccountStatus.ACTIVE;
    }

    public void close(LocalDateTime closedAt) {
        if (isClosed()) {
            throw new AccountOperationException("Account is already closed");
        }
        this.status = AccountStatus.CLOSED;
        this.closedAt = closedAt;
    }
}

