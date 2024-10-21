package faang.school.accountservice.model.entity;

import faang.school.accountservice.model.enums.PaymentAccountStatus;
import faang.school.accountservice.model.enums.PaymentAccountType;
import jakarta.persistence.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "number", unique = true, nullable = false, length = 20)
    private Long number;

    //TODO владелец пользователь или проект

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentAccountType type;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentAccountStatus status;

    @Column(name = "createdAt",nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt",nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    @Column(name = "closedAt",nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime closedAt;

    @Column(name = "version",nullable = false)
    private Long version;
}
