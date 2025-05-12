package faang.school.accountservice.entity.free_account;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class FreeAccountNumberId implements Serializable {

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 50)
    private AccountType accountType;

    @Column(name = "account_number", nullable = false, length = 20)
    private String accountNumber;
}
