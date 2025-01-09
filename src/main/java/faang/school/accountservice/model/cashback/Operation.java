package faang.school.accountservice.model.cashback;

import faang.school.accountservice.enums.OperationStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "operation")
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "merchant_id")
    private Long merchantId;

    @Column(name = "operation_type_id")
    private Long operationTypeId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OperationStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "cashback_processed")
    private Boolean cashbackProcessed = false;

    @Column(name = "cashback_processed_at")
    private LocalDateTime cashbackProcessedAt;
}