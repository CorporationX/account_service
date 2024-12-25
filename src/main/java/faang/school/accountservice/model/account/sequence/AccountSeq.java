package faang.school.accountservice.model.account.sequence;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "account_numbers_sequence")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountSeq {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 32)
    private AccountType type;

    @Column(name = "counter", nullable = false)
    private Long counter;

    @Version
    private Integer version;
}

