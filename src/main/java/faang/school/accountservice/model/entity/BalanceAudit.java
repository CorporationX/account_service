package faang.school.accountservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "balance_audit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "balance_version", nullable = false)
    private int balanceVersion;

    @Column(name = "authorized_amount", nullable = false)
    private BigDecimal authorizedAmount;

    @Column(name = "actual_amount", nullable = false)
    private BigDecimal actualAmount;

    @Column(name = "operation_id", nullable = false)
    private UUID operationId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}