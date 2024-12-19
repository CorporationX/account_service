package faang.school.accountservice.util;

import faang.school.accountservice.enums.AccountType;
import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class AccountNumberUtil {

    public String generateAccountNumber(AccountType accountType) {
        int length = getLengthByAccountType(accountType);
        return generateRandomNumber(length);
    }

    private int getLengthByAccountType(AccountType accountType) {
        return switch (accountType) {
            case INDIVIDUAL -> 12;
            case LEGAL -> 20;
            case SAVINGS -> 16;
        };
    }

    private String generateRandomNumber(int length) {
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder();

        for (int i = 0; i < length; i++) {
            accountNumber.append(random.nextInt(10));
        }

        return accountNumber.toString();
    }
}
