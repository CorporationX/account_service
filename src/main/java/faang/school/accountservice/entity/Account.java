package faang.school.accountservice.entity;

import faang.school.accountservice.enums.CurrencyType;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.enums.StatusType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
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
    private BigDecimal balance = BigDecimal.ZERO;

    /** Владелец счета */
    @Enumerated(EnumType.STRING)
    @Column(name = "owner", nullable = false)
    private OwnerType owner;

    /** Тип счета */
    @Column(name = "type", nullable = false)
    private String type;

    /** Код валюты */
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private CurrencyType currencyType;

    /** Статус: действующий, замороженный или закрытый */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusType status;

    /** Время создания счета */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Время изменения счета */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** Время закрытия счета */
    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    /** Версия счета */
    @Column(name = "account_version")
    private String accountVersion;
}
