package faang.school.accountservice.util;

import faang.school.accountservice.enums.AccountType;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Component
public class AccountNumberGeneratorImpl implements AccountNumberGenerator {

    private static final int MAX_LENGTH_ACCOUNT_NUMBER = 20;

    @Override
    public String generateAccountNumber(AccountType type, Long number) {
        if (number.toString().length() > 16) {
            throw new IllegalArgumentException("Number length exceeds the maximum allowed length of 16 digits");
        }

        String code = type.getCode().toString();
        StringBuilder accountNumber = new StringBuilder(code);

        int centerSpace = MAX_LENGTH_ACCOUNT_NUMBER - number.toString().length() - code.length();
        IntStream.range(0, centerSpace).forEach(i -> accountNumber.append(0));
        accountNumber.append(number);

        return accountNumber.toString();
    }
}
