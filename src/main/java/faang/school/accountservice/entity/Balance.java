package faang.school.accountservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "balance")
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(name = "account_id", unique = true)
    @OneToOne(cascade = CascadeType.ALL)
    private Account account;

    @Column(name = "cur_auth_balance")
    private BigDecimal curAuthBalance;

    @Column(name = "cur_fact_balance")
    private BigDecimal curFactBalance;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private int version;
}
