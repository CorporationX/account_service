package faang.school.accountservice.model;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Embeddable
@Builder
@AllArgsConstructor
public class FreeAccountNumberId {

    @Column(name = "account_type", length = 24, nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(name = "number", length = 20, nullable = false, unique = true)
    private String number;
}