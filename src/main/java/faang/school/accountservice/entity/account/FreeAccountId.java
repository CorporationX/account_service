package faang.school.accountservice.entity.account;

import faang.school.accountservice.enums.account.AccountEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;

@Embeddable
@AllArgsConstructor
public class FreeAccountId {

    @Column(name = "type", nullable = false, length = 20)
    @Enumerated(value = EnumType.STRING)
    private AccountEnum type;

    @Column(name = "account_number", nullable = false)
    private long accountNumber;
}
