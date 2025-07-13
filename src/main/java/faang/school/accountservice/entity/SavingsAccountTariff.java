package faang.school.accountservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "savings_account_tariff")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavingsAccountTariff {

    @EmbeddedId
    private SavingsAccountTariffId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("savingsAccountId")
    @JoinColumn(name = "savings_account_id")
    private SavingsAccount savingsAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tariffId")
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;
}
