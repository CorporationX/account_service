package faang.school.accountservice.validator.account;

import faang.school.accountservice.dto.account.CreateAccountDto;
import org.springframework.stereotype.Component;

@Component
public class AccountServiceValidator {
    public void checkId(long... id) {
        for (int i = 0; i < id.length; i++) {
            if (id[i] < 0) {
                throw new IllegalArgumentException("Id must be > 0");
            }
        }
    }

    public void validateCreateAccountDto(CreateAccountDto createAccountDto) {
        if (createAccountDto == null) {
            throw new IllegalArgumentException("CreateAccountDto cant be null");
        }

        String accountNumber = createAccountDto.getAccountNumber();

        if (accountNumber == null || accountNumber.isBlank() || accountNumber.length() < 12 || accountNumber.length() > 20) {
            throw new IllegalArgumentException("Illegal account number");
        }

        if (createAccountDto.getType() == null || createAccountDto.getCurrency() == null || createAccountDto.getOwner() == null) {
            throw new IllegalArgumentException("Type, Currency and Owner must not be null");
        }

    }
}
