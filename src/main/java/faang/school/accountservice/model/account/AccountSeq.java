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
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 32)
    private AccountType type;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq_generator")
    @SequenceGenerator(name = "account_seq_generator", sequenceName = "account_number_seq", allocationSize = 1)
    @Column(name = "counter", nullable = false)
    private long counter;

    @Transient
    private long initialValue;
}
