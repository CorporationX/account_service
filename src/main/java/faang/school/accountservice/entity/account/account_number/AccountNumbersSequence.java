package faang.school.accountservice.entity.account.account_number;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "account_numbers_sequences")
public class AccountNumbersSequence {

    @Id
    @Column(name = "account_type", nullable = false, length = 8)
    @Enumerated(value = EnumType.STRING)
    private AccountType accountType;

    @Column(name = "counter", nullable = false)
    private long counter;

    @Transient
    private long initialValue;
}
