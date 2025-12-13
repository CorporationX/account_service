package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.exception.AccountValidateException;
import faang.school.accountservice.model.AccountStatusType;
import org.springframework.stereotype.Component;

@Component
public class ValidateAccount {

    public void validateAccount(AccountDto accountDto) {
        if (accountDto.status().equals(AccountStatusType.CLOSED)
                || accountDto.status().equals(AccountStatusType.FROZEN)) {
            throw new AccountValidateException("Account status is invalid" + accountDto.status());
        }
    }
}