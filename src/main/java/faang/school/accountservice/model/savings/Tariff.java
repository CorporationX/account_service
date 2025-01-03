package faang.school.accountservice.model.savings;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tariff")
public class Tariff {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "rate_history")
  private String rateHistory;

  @Column(name = "version")
  @Version
  private Long version;

  public void addNewRate(BigDecimal value) {
    rateHistory = rateHistory.replace("]", ", " + value.toString() + "%]");
    version++;
  }

  //TODO
  public BigDecimal getCurrentRate() {
    String [] str = rateHistory.split(",");
    String rate = str[str.length - 1].replace("[%]", "");
    return new BigDecimal(rate);
  }

}
