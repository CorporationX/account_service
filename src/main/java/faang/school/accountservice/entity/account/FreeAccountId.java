package faang.school.accountservice.entity.account;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@Data
@AllArgsConstructor
public class FreeAccountId {
    @Column(name = "type", nullable = false, length = 16)
    @Enumerated(value = EnumType.STRING)
    private AccountType type;

    @Column(name = "account_number", nullable = false, length = 20)
    private String accountNumber;
}