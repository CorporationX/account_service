package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "balance_audit")
public class BalanceAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "account_number", length = 20, nullable = false, unique = true, updatable = false)
    private String accountNumber;
    @JoinColumn(name = "balance_version", nullable = false)
    private Long balanceVersion;
    @Column(name = "authorized_amount", precision = 30, scale = 10, nullable = false)
    private BigDecimal authorizedAmount;
    @Column(name = "actual_amount", precision = 30, scale = 10, nullable = false)
    private BigDecimal actualAmount;
    @JoinColumn(name = "balance_change_id")
    private Long balanceChangeId;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
}


/*
CREATE TABLE balance_audit (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_number varchar(20) UNIQUE NOT NULL,
    balance_version bigint NOT NULL,
    authorized_amount NUMERIC(30, 10) NOT NULL,
    actual_amount NUMERIC(30, 10) NOT NULL,
    balance_change_id bigint NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp
);

  - include:
      file: db/changelog/changeset/V003__balance-audit-table-init.sql
 */