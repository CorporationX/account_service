package faang.school.accountservice.model.entity;

import faang.school.accountservice.model.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FreeAccountNumberId {

    @Column(name = "account_type", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(name = "account_number", nullable = false, length = 20)
    @Size(min = 12, max = 20, message = "Length of the account number must be between 12 and 20 characters")
    private Long accountNumber;
}
