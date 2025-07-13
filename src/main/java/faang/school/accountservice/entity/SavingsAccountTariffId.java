package faang.school.accountservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SavingsAccountTariffId implements Serializable {

    @Column(name = "savings_account_id")
    private Long savingsAccountId;

    @Column(name = "tariff_id")
    private Long tariffId;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;
}

