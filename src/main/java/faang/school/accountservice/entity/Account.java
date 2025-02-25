package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.InvoiceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 32, nullable = false)
    private String invoice;

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

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Version
    private int version;
}
