package faang.school.accountservice.entity.account.account_number;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreeAccountNumberId {

    @Column(name = "account_type", length = 8, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountType accountType;

    @Column(name = "account_number", nullable = false)
    private long accountNumber;
}
