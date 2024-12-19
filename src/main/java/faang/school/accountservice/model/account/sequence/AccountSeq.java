package faang.school.accountservice.model.account.sequence;

import faang.school.accountservice.model.account.freeaccounts.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_number_sequence")
@Data
@NoArgsConstructor
public class AccountSeq {

    @Id
    @Column(name = "type", nullable = false, length = 32)
    private AccountType type;

    @Column(name = "counter", nullable = false)
    private long counter;
}
