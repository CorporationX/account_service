package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Version;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Table(name = "accounts")
public class Account {

    /** Уникальный идентификатор счета */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Номер счета */
    @Column(name = "account", nullable = false, unique = true)
    private String account;

    /** Баланс счета */
    @Builder.Default
    @Column(name = "balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal accountBalance = BigDecimal.ZERO;

    @OneToOne(mappedBy = "account")
    private Balance balance;

    /** Владелец счета */
    @Enumerated(EnumType.STRING)
    @Column(name = "owner", nullable = false)
    private OwnerType ownerType;

    /** Тип счета */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AccountType accountType;

    /** Код валюты */
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency;

    /** Статус: действующий, замороженный или закрытый */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus accountStatus;

    /** Время создания счета */
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /** Время изменения счета */
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /** Время закрытия счета */
    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    /** Версия счета */
    @Version
    @Column(name = "account_version")
    private String accountVersion;
}
