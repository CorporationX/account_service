package faang.school.accountservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(min = 12, max = 20)
    @Column(name = "number", nullable = false, unique = true)
    private String number;

    @Column(name = "owner_id")
    private long ownerId;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Size(min = 3, max = 3)
    @Column(name = "currency", nullable = false, columnDefinition = "USD")
    private String currency;

    @Column(name = "status", nullable = false)
    private AccountStatus status;

    @Column(name = "balance")
    private BigDecimal balance;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Version
    private Integer version;
}
