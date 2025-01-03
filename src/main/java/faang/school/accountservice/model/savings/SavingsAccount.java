package faang.school.accountservice.model.savings;

import faang.school.accountservice.model.account.Account;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "savings_account")
public class SavingsAccount {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "account_id", referencedColumnName = "id")
  private Account account;

  @Column(name = "tariff_history", nullable = false)
  private String tariffHistory;

  @Column(name = "last_income_at")
  private LocalDate lastIncomeAt;

  @Column(name = "version", nullable = false)
  @Version
  private Long version;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public Long getCurrentTariffId() {
    String [] str = tariffHistory.split(",");
    int index = str.length - 1;
    return Long.valueOf(str[index].trim().replaceAll("[\\[%\\]]", ""));
  }

}
