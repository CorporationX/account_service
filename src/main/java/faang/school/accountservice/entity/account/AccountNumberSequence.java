package faang.school.accountservice.entity.account;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account_number_sequence")
public class AccountNumberSequence {

    @Id
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountType accountType;

    @Column(name = "current_value", nullable = false)
    private long currentValue;

}
