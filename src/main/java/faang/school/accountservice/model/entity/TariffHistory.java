package faang.school.accountservice.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.springframework.data.relational.core.mapping.Table;

@Entity
@Table(name = "tariff_history")
public class TariffHistory {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "savings_account_id")
    private SavingsAccount savingsAccount;
}
