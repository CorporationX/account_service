package faang.school.accountservice.entity;

import faang.school.accountservice.enums.Currency;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @Column(length = 20, nullable = false, unique = true)
    private String id;

    @Column(name = "balance", precision = 19, scale = 4, nullable = false)
    @NotNull
    private BigDecimal balance;

    @Column(name = "owner", nullable = false)
    private Owner owner;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "type", nullable = false)
    private Type type;

    @Column(name = "currency")
    private Currency currency;

    @Column(name = "status", nullable = false)
    private Status status;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "version")
    private String version;

    public enum Status {
        ACTIVE, CLOSED, FROZEN
    }

    public enum Type {
        CURRENT, SETTLEMENT, CREDIT, DEPOSIT, BUDGET
    }

    public enum Owner {
        PROJECT, USER
    }
}
