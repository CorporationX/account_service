package faang.school.accountservice.entity.account;

import faang.school.accountservice.enums.account.AccountEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "account_numbers_sequence")
public class AccountNumbersSequence {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountEnum accountType;

    @Column(name = "current_counter", nullable = false)
    private Long currentCounter;

    // getters and setters
}
