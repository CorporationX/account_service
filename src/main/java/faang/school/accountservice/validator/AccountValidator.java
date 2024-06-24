package faang.school.accountservice.validator;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

import static faang.school.accountservice.exception.message.AccountExceptionMessage.CLOSED_ACCOUNT_CREATE_EXCEPTION;
import static faang.school.accountservice.exception.message.AccountExceptionMessage.NO_OWNER_EXCEPTION;
import static faang.school.accountservice.exception.message.AccountExceptionMessage.TWO_OWNERS_EXCEPTION;

@Component
public class AccountValidator {
    public void validateAccountOwners(AccountDto accountDto) {
        if (accountDto.getOwnerUserId() == null && accountDto.getOwnerProjectId() == null) {
            throw new DataValidationException(NO_OWNER_EXCEPTION.getMessage());
        }

        if (accountDto.getOwnerUserId() != null && accountDto.getOwnerProjectId() != null) {
            throw new DataValidationException(TWO_OWNERS_EXCEPTION.getMessage());
        }
    }

    public void validateCreationStatus(AccountDto accountDto) {
        if (accountDto.getStatus().equals(AccountStatus.CLOSED)) {
            throw new DataValidationException(CLOSED_ACCOUNT_CREATE_EXCEPTION.getMessage());
        }
    }
}

