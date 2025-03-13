package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountType;
import org.springframework.stereotype.Component;

@Component
public class DebitAccountNumber implements AccountNumber {
    private static final long DEBIT_ACCOUNT_PATTERN = 4200_0000_0000_0000L;

    @Override
    public AccountType getAccountType() {
        return AccountType.DEBIT;
    }

    @Override
    public Long getAccountNumberPattern() {
        return DEBIT_ACCOUNT_PATTERN;
    }
}
