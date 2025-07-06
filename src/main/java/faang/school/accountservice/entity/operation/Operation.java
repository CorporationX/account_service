package faang.school.accountservice.entity.operation;

import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "currency")
public class Operation extends BaseEntity {
    @Column(name = "operation_token", nullable = false, updatable = false, unique = true)
    private UUID operationToken;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // TODO: не уверен что нужно именно так, если это не оплата, а начисление процентов по НС, то from пустое
    @ManyToOne
    @JoinColumn(name = "balance_from_id", nullable = false)
    private Balance balanceFrom;

    @ManyToOne
    @JoinColumn(name = "balance_to_id", nullable = false)
    private Balance balanceTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private OperationType type;

    // TODO: пока что не понятно для чего
    @Column(name = "locked_by", length = 128)
    private String lockedBy;

    @Column(name = "active", nullable = false)
    private boolean active = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OperationStatus status;

    @Column(name = "details", length = 4000)
    private String details;
}
