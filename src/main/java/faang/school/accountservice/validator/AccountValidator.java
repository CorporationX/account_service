package faang.school.accountservice.validator;

import faang.school.accountservice.dto.account.AccountDtoOpen;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.owner.OwnerType;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class AccountValidator {
    public void validateAccountForOpen(AccountDtoOpen dto) {
        checkOwnerType(dto.getOwnerType());
        checkAccountType(dto.getAccountType());
        checkCurrency(dto.getCurrency());
    }

    private void checkCurrency(Currency currency) {
        if (currency == null || !EnumSet.allOf(Currency.class).contains(currency)) {
            throw new IllegalArgumentException("Invalid currency.");
        }
    }

    private void checkAccountType(AccountType accountType) {
        if (accountType == null || !EnumSet.allOf(AccountType.class).contains(accountType)) {
            throw new IllegalArgumentException("Invalid account type.");
        }
    }

    private void checkOwnerType(OwnerType ownerType) {
        if (!EnumSet.allOf(OwnerType.class).contains(ownerType)) {
            throw new IllegalArgumentException("Invalid owner type.");
        }
    }
}
