package faang.school.accountservice.model.cashback;

import faang.school.accountservice.model.account.Account;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "operation_type", nullable = false)
    private String operationType;

    @Column(name = "merchant_category", nullable = false)
    private String merchantCategory;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "merchant_id", nullable = false)
    private String merchantId;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;
}