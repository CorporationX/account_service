package faang.school.accountservice.model;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
public class FreeAccountId {

    @Column(name = "accounttype", nullable = false, length = 32)
    @Enumerated(value = EnumType.STRING)
    private AccountType accountType;

    @Column(name = "account_number", nullable = false)
    private long accountNumber;
}
