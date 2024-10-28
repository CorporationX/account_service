package faang.school.accountservice.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tariff_history")
public class TariffHistory {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "savings_account_id")
    @JsonIgnore
    private SavingsAccount savingsAccount;
}
