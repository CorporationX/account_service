package faang.school.accountservice.model.account;

import faang.school.accountservice.model.AccountStatus;
import faang.school.accountservice.model.AccountType;
import faang.school.accountservice.model.Currency;
import faang.school.accountservice.model.OwnerType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(min = 12, max = 20, message = "Number must be between 12 and 20 characters")
    @Column(name = "number", length = 20, nullable = false, unique = true)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false)
    private OwnerType owner;

    @Column(name = "owner_id", nullable = false)
    private long ownerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    private AccountStatus accountStatus;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "close_at")
    private LocalDateTime closeAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;
}
