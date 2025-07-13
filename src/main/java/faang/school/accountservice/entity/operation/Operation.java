package faang.school.accountservice.entity.operation;

import faang.school.accountservice.entity.account.Account;
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
@Table(name = "operation")
public class Operation extends BaseEntity {
    // TODO: уникальный индекс на тип и токен
    @Column(name = "operation_token", nullable = false, updatable = false, unique = true)
    private UUID operationToken;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "account_from_id", updatable = false)
    private Account accountFrom;

    @ManyToOne
    @JoinColumn(name = "account_to_id", updatable = false)
    private Account accountTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false)
    private OperationType type;

    // TODO: пока что не понятно для чего
    // TODO: если приходит клиринг операции, для баланса в котором проведена авторизация,
    // TODO: то отправлять в топик ошибок и в payment сервисе запускать повторный клиринг через какое-то время
    // TODO: можно в account service тоже джобу, которая снимает блокировки раз в n минут
    @Column(name = "locked_by", length = 128)
    private String lockedBy;

    // TODO: json
    @Column(name = "storage", length = 4000)
    private String storage;

    @Column(name = "active", nullable = false)
    private boolean active = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OperationStatus status;

    @Column(name = "details", length = 4000)
    private String details;
}
