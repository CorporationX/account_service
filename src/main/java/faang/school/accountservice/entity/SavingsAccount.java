package faang.school.accountservice.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "savings_accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;

    @Column(name = "last_date_before_interest", nullable = false)
    private LocalDateTime lastDateBeforeInterest;

    @CreationTimestamp
    @Column(name = "date_of_creation", nullable = false)
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "date_of_update")
    private LocalDateTime updateAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
