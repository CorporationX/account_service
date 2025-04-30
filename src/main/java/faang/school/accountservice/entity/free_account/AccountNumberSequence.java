package faang.school.accountservice.entity.free_account;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "account_number_sequence")
public class AccountNumberSequence {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", length = 50)
    private AccountType accountType;

    @Column(name = "last_value", nullable = false)
    private String lastValue;

    @Version
    private Long version;
}
