package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDtoFilter {
    @Positive
    private Long id;

    @Pattern(regexp = "^[0-9]{12,20}$", message = "Account number must be between 12 and 20 digits and contain only numbers.")
    private String accountNumber;

    @Positive
    private Long ownerId;

    private OwnerType ownerType;
    private AccountType accountType;
    private Currency currency;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private Boolean isVerified;

    @Size(max = 4096)
    private String notes;
}
