package faang.school.accountservice.model.account;

import faang.school.accountservice.model.account.enums.AccountType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_number_sequence")
@Data
@NoArgsConstructor
public class AccountSeq {
    @Id
    @Column(name = "type", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column(name = "counter", nullable = false)
    private long counter;

    @Transient
    private long initialValue;
}
