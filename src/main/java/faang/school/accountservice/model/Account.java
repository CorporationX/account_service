package faang.school.accountservice.model;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Currency;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "number")
    private Long number;

    @Column(name = "user_id")
    private Long user_id;

    @Column(name = "project_id")
    private Long project_id;

    @Column(name = "type")
    private String type;

    @Column(name = "currency_code")
    private Currency currency;

    @Column(name = "status")
    private AccountStatus status;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @Column(name = "closed_at")
    private LocalDateTime closed_at;

    @Column(name = "version")
    private int version;
}
