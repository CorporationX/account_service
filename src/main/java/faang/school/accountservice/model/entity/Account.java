package faang.school.accountservice.model.entity;

import faang.school.accountservice.model.enums.AccountStatus;
import faang.school.accountservice.model.enums.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = "\\d{12,20}", message = "Account number must be between 12 and 20 digits")
    @Column(name = "number", unique = true, nullable = false, length = 20)
    private Long number;

    //TODO владелец пользователь или проект

    @OneToOne()
    @JoinColumn(name="project_id")
    private Project project;

    @OneToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(name = "createdAt", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    @Column(name = "closedAt", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime closedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
