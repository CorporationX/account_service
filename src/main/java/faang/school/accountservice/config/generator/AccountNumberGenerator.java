package faang.school.accountservice.config.generator;

import faang.school.accountservice.config.account.AccountProperties;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class AccountNumberGenerator {

    private final int minAccountNumberLength;
    private final int maxAccountNumberLength;

    public AccountNumberGenerator(AccountProperties accountProperties) {
        this.minAccountNumberLength = accountProperties.getNameLength().getMin();
        this.maxAccountNumberLength = accountProperties.getNameLength().getMax();

        if (minAccountNumberLength > maxAccountNumberLength) {
            throw new IllegalArgumentException("Min should be less or equals max");
        }
    }

    public String generateRandomAccountNumberInRange() {
        return RandomStringUtils.randomNumeric(minAccountNumberLength, maxAccountNumberLength);
    }
}
