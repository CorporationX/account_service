package faang.school.accountservice.model.savings;

import faang.school.accountservice.model.account.Account;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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

  @OneToOne(cascade = CascadeType.ALL)
  @MapsId
  @JoinColumn(name = "account_id", referencedColumnName = "id")
  private Account account;

  @Column(name = "tariff")
  private String tariff;

  @Column(name = "payment_date")
  private LocalDate paymentDate;

  @Column(name = "version", nullable = false)
  private Long version;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

}
