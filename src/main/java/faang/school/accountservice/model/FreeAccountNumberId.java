package faang.school.accountservice.model;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public  class FreeAccountNumberId implements Serializable {
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 50)
    private AccountType accountType;

    @Column(name = "number", nullable = false)
    private BigInteger number;
}