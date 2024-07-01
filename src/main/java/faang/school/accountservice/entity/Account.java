package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account")
@OptimisticLocking(type = OptimisticLockType.ALL)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "number", nullable = false, unique = true)
    private String number;

    @Column(name = "user_id")
    private Long ownerId;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "account_type")
    @Enumerated(EnumType.ORDINAL)
    private AccountType type;

    @Column(name = "currency")
    @Enumerated(EnumType.ORDINAL)
    private Currency currency;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @OneToOne(mappedBy = "account")
    private Balance balance;

    @PreUpdate
    protected void onUpdate() {
        if (Status.CLOSED.equals(this.status) && this.closedAt == null) {
            closedAt = LocalDateTime.now();
        }
    }
}
