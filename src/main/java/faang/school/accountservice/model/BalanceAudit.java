package faang.school.accountservice.model;

import faang.school.accountservice.dto.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "balance_audit")
@Getter
@Setter
public class BalanceAudit {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "payment_operation_id", nullable = false)
    private UUID paymentOperationId;

    @Column(name = "auth_balance_change", nullable = false)
    private BigDecimal authBalanceChange;

    @Column(name = "clear_balance_change", nullable = false)
    private BigDecimal clearBalanceChange;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_code", nullable = false, length = 3)
    private Currency currency;
}
