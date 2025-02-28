package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true, length = 20)
    private String number;

    @Column(name = "owner_id", nullable = false)
    private long ownerId;

    @Column(name = "owner_type", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private OwnerType ownerType;

    @Column(name = "type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column(nullable = false, length = 5)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "closed_date", nullable = false)
    private LocalDateTime closedDate;

    @Version
    @Column(nullable = false)
    private long version;
}
