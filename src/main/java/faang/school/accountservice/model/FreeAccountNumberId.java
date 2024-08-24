package faang.school.accountservice.model;

import faang.school.accountservice.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public  class FreeAccountNumberId implements Serializable {
    @Column(name = "account_type", nullable = false, length = 50)
    private AccountType accountType;

    @Column(name = "number", nullable = false)
    private String number;
}