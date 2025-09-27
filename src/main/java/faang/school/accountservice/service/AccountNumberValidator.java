package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.InvalidAccountNumberException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountNumberValidator {

    public void validate(AccountType accountType, String accountNumber) {
        if (!accountType.isValidAccountNumber(accountNumber)) {
            throw new InvalidAccountNumberException(
                    String.format("Invalid account number '%s' for type %s. " +
                                    "Expected format: %s + 8-16 digits",
                            accountNumber, accountType, accountType.getPrefix()));
        }
    }

    public boolean isValid(AccountType accountType, String accountNumber) {
        return accountType.isValidAccountNumber(accountNumber);
    }
}