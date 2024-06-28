package faang.school.accountservice.validator;

import faang.school.accountservice.dto.AccountDto;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AccountValidator {

    public void initValidation(AccountDto accountDto) {
        validateAuthorExist(accountDto);
        validateStatusSelection(accountDto);
        validateCurrencySelection(accountDto);
    }

    public void validateAuthorExist(AccountDto accountDto) {
        if (Objects.isNull(accountDto.getOwnerId()) || Objects.isNull(accountDto.getProjectId())) {
            throw new IllegalArgumentException("Account must be have an owner");
        }
    }

    public void validateStatusSelection(AccountDto accountDto) {
        if (Objects.isNull(accountDto.getStatus())) {
            throw new IllegalArgumentException("Account must be have selected status");
        }
    }

    public void validateCurrencySelection(AccountDto accountDto) {
        if (Objects.isNull(accountDto.getCurrency())) {
            throw new IllegalArgumentException("Account must be have selected currency");
        }
    }
}
