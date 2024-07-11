package faang.school.accountservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "savings_accounts")
public class SavingsAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "accounts_id", referencedColumnName = "id")
    private Account account;

    @OneToMany(mappedBy = "savingsAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TariffHistory> tariffHistory;

    @Version
    private Integer version;

    @Column(name = "last_interest_date")
    private LocalDate lastInterestDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
