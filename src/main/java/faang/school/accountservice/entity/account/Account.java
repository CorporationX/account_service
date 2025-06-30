package faang.school.accountservice.entity.account;

import faang.school.accountservice.entity.base.BaseEntityWithVersion;
import faang.school.accountservice.entity.currency.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "account")
public class Account extends BaseEntityWithVersion {

    @Column(name = "number", nullable = false, unique = true, updatable = false, length = 128)
    private String number;

    @Column(name = "user_id", updatable = false)
    private Long userId;

    @Column(name = "project_id", updatable = false)
    private Long projectId;

    @Column(name = "owner_type", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private AccountOwnerType ownerType;

    @Column(name = "type", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @ManyToOne
    @JoinColumn(name = "currency_id", nullable = false, updatable = false)
    private Currency currency;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;
}
