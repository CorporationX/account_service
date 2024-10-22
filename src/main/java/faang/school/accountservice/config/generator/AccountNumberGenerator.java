package faang.school.accountservice.config.generator;

import faang.school.accountservice.config.account.AccountProperties;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {

    private final AccountProperties accountProperties;

    public String generateRandomAccountNumberInRange() {
        int minLength = accountProperties.getNameLength().getMin();
        int maxLength = accountProperties.getNameLength().getMax();

        if (minLength >= maxLength) {
            throw new IllegalArgumentException("Min should be less or equals max");
        }

        return RandomStringUtils.randomNumeric(minLength, maxLength);
    }
}
