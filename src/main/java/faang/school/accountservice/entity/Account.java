package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.InvoiceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Account extends BaseEntity {

    @Column(name = "account_number", length = 32, nullable = false)
    private String accountNumber;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "project_id")
    private Long projectId;

    @Column(nullable = false, name = "invoice_type", length = 32)
    @Enumerated(EnumType.STRING)
    private InvoiceType invoiceType;

    @Column(nullable = false, length = 3)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(nullable = false, length = 16)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Version
    private int version;
}
