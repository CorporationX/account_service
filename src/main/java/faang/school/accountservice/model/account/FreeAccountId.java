package faang.school.accountservice.model.account;

import faang.school.accountservice.model.account.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;

@Embeddable
@AllArgsConstructor
public class FreeAccountId {

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private AccountType type;

    @Column(name = "account_number", nullable = false)
    private long accountNumber;
}
