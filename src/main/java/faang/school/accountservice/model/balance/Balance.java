package faang.school.accountservice.model.balance;

import faang.school.accountservice.model.account.Account;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
@Table(name = "balance")
public class Balance {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "account_id", referencedColumnName = "id")
  private Account account;

  @Column(name = "authorized_value")
  private BigDecimal authorizedValue;

  @Column(name = "actual_value")
  private BigDecimal actualValue;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "version", nullable = false)
  private long version;

  public void authorizePayment(BigDecimal value) {
    authorizedValue = authorizedValue.subtract(value);
    version++;
  }

  public void clearPayment(BigDecimal value) {
    actualValue = actualValue.subtract(value);
    version++;
  }

  public void upBalance(BigDecimal value) {
    actualValue = actualValue.add(value);
    authorizedValue = authorizedValue.add(value);
    version++;
  }
}
