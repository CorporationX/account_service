package faang.school.accountservice.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "savings_account")
@Data
@Builder
public class SavingsAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "last_interest_calculated_date", nullable = false)
    private LocalDateTime lastInterestCalculatedDate;

    @Column(name = "version")
    @Version
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "id")
    private Account account;

    @OneToMany(mappedBy = "savingsAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavingsAccountTariffHistory> tariffHistory;

    @Builder
    public SavingsAccount(Long id, LocalDateTime lastInterestCalculatedDate, Long version, LocalDateTime createdAt, LocalDateTime updatedAt, Account account, List<SavingsAccountTariffHistory> tariffHistory) {
        this.id = id;
        this.lastInterestCalculatedDate = lastInterestCalculatedDate;
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.account = account;
        this.tariffHistory = tariffHistory != null ? tariffHistory : new ArrayList<>();
    }

    public SavingsAccount() {
        this.tariffHistory = new ArrayList<>();
    }
}
