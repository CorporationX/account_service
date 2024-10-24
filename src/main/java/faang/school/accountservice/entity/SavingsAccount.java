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
    @Positive
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @OneToOne(mappedBy = "savingsAccount")
    private Tariff tariff;

    @OneToMany(mappedBy = "savingsAccount", cascade = CascadeType.ALL)
    private List<TariffHistory> tariffHistory;

    @Column(name = "last_date_before_interest")
    private LocalDateTime lastDateBeforeInterest;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @CreationTimestamp
    @Column(name = "date_of_creation")
    private LocalDateTime dateOfCreation;

    @UpdateTimestamp
    @Column(name = "date_of_update")
    private LocalDateTime dateOfUpdate;
}
