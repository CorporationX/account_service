package faang.school.accountservice.validator;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.DataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AccountValidator {

    public void validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            log.error("The number of account is null or blank");
            throw new DataValidationException("The number of account is null or blank");
        }
        if (!accountNumber.matches("\\d+")) {
            log.error("The number of account must contain only numbers");
            throw new DataValidationException("The number of account must contain only numbers");
        }
        if (accountNumber.length() < 12 || accountNumber.length() > 20) {
            log.error("The number account length must be from 12 to 20 characters.");
            throw new DataValidationException("The number account length must be from 12 to 20 characters.");
        }
    }

    public void validateAccountOwner(OwnerType ownerType, Long ownerProjectId, Long ownerUserId) {

        if (ownerProjectId == null && ownerUserId == null) {
            log.error("Owner project_id and Owner user_id cannot be both null");
            throw new DataValidationException("Owner project_id and Owner user_id cannot be both null");
        }

        if (ownerType.equals(OwnerType.PROJECT)) {
            if (ownerUserId != null && ownerProjectId == null) {
                log.error("Owner project ID is null, owner user id is not null - Owner Type mismatch");
                throw new DataValidationException("Owner project ID is null, owner user id is not null - Owner Type mismatch");
            }
        } else if (ownerType.equals(OwnerType.USER)) {
            if (ownerProjectId != null && ownerUserId == null) {
                log.error("Owner project ID is not null, owner user id is  null - Owner Type mismatch");
                throw new DataValidationException("Owner project ID is null, owner user id is not null - Owner Type mismatch");
            }
        }
    }
}
