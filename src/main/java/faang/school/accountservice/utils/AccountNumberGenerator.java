package faang.school.accountservice.utils;

import jakarta.validation.constraints.Digits;

import java.security.SecureRandom;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class AccountNumberGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String DIGITS = "0123456789";
    private static final String PREFIX = "BANK";
    private static final Integer ACCOUNT_LENGTH = 20 - PREFIX.length();

    public static Set<String> generateAccountNumber(int size) {
        CopyOnWriteArraySet<String> accountNumbers = new CopyOnWriteArraySet<>();

        while (accountNumbers.size() < size) {
            StringBuffer stringBuffer = new StringBuffer(PREFIX);
            for (int i = 0; i < ACCOUNT_LENGTH; i++) {
                stringBuffer.append(DIGITS.charAt(secureRandom.nextInt(DIGITS.length())));
            }
            accountNumbers.add(stringBuffer.toString());
        }
        return accountNumbers;
    }
}