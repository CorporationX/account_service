package faang.school.accountservice.entity.account;

import faang.school.accountservice.enums.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, length = 20, unique = true)
    private String account_number;

    @Column(name = "owner_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private OwnerType owner_type;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountType accountType;

    @Column(name = "currency", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Currency currency;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Version
    @Column(name = "version")
    private Integer version = 1;

    public void setStatus(AccountStatus status) {
        this.status = status;
        if (status == AccountStatus.CLOSED) {
            this.closedAt = LocalDateTime.now();
        }
    }
}
