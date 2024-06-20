package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.DataValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static faang.school.accountservice.exception.message.AccountExceptionMessage.CLOSED_ACCOUNT_CREATE_EXCEPTION;
import static faang.school.accountservice.exception.message.AccountExceptionMessage.NO_OWNER_EXCEPTION;
import static faang.school.accountservice.exception.message.AccountExceptionMessage.TWO_OWNERS_EXCEPTION;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {
    private Long id;

    @NotNull(message = "An account must have a number")
    @NotBlank(message = "An account must have a not blank number")
    @Size(min = 12, max = 20, message = "An account must have a number with size between 12 and 20 symbols")
    private String number;

    private Long ownerUserId;

    private Long ownerProjectId;

    @NotNull(message = "An account must have a type")
    private AccountType type;

    @NotNull(message = "An account must have a currency")
    private Currency currency;

    @NotNull(message = "An account must have a status")
    private AccountStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime closedAt;


    public void validateAccountOwners() {
        if (ownerUserId == null && ownerProjectId == null) {
            throw new DataValidationException(NO_OWNER_EXCEPTION.getMessage());
        }

        if (ownerProjectId != null && ownerUserId != null) {
            throw new DataValidationException(TWO_OWNERS_EXCEPTION.getMessage());
        }
    }

    public void validateCreationStatus() {
        if (status.equals(AccountStatus.CLOSED)) {
            throw new DataValidationException(CLOSED_ACCOUNT_CREATE_EXCEPTION.getMessage());
        }
    }
}
