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
@Table(name = "savings_account")
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

    @OneToOne(mappedBy = "savingsAccount", fetch = FetchType.LAZY)
    private Tariff tariff;

    @Column(name = "last_date_before_interest", nullable = false)
    private LocalDateTime lastDateBeforeInterest;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @CreationTimestamp
    @Column(name = "date_of_creation", nullable = false)
    private LocalDateTime dateOfCreation;

    @UpdateTimestamp
    @Column(name = "date_of_update")
    private LocalDateTime dateOfUpdate;
}
