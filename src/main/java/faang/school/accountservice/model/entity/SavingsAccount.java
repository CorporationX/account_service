package faang.school.accountservice.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "savings_account")
public class SavingsAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @OneToOne
    @JoinColumn(name = "account_number", referencedColumnName = "number", insertable = false, updatable = false)
    @JsonIgnore
    private Account account;

    @OneToMany(mappedBy = "savingsAccount")
    private List<TariffHistory> tariffHistory;

    @Column(name = "last_date_percent")
    private LocalDateTime lastDatePercent;

    @Version
    @Column(name = "version", insertable = false)
    private Long version;

    @Column(name = "created_at", insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;

    // Новые поля
    @Column(name = "rate_bonus")
    private BigDecimal rateBonus;  // Используем BigDecimal для финансовых вычислений

    @Column(name = "last_bonus_update")
    private LocalDateTime lastBonusUpdate;
}
