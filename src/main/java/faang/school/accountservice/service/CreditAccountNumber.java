package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountType;
import org.springframework.stereotype.Component;

@Component
public class CreditAccountNumber implements AccountNumber {
    private static final long CREDIT_ACCOUNT_PATTERN = 5236_0000_0000_0000L;

    @Override
    public AccountType getAccountType() {
        return AccountType.CREDIT;
    }

    @Override
    public Long getAccountNumberPattern() {
        return CREDIT_ACCOUNT_PATTERN;
    }
}
