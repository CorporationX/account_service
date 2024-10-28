package faang.school.accountservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "savings_account_rate")
public class SavingsAccountRate {

    @Id
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    @Column(name = "rate")
    private double rate;
}
