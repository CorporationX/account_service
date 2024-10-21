package faang.school.accountservice.validator;

import faang.school.accountservice.annotation.OneHolderAccount;
import faang.school.accountservice.dto.AccountDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OneHolderAccountValidator implements ConstraintValidator<OneHolderAccount, AccountDto> {

    @Override
    public boolean isValid(AccountDto accountDto, ConstraintValidatorContext context) {
        if (accountDto == null) {
            return false;
        }
        boolean hasUserId = accountDto.getHolderUserId() != null;
        boolean hasProjectId = accountDto.getHolderProjectId() != null;

        return hasUserId ^ hasProjectId;
    }
}

