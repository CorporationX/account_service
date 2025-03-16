package faang.school.accountservice.utils;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

public class AccountNumberGenerator {
    private static final String DIGITS = "0123456789";
    private static final int NUMBER_LENGTH = 20;
    private static final SecureRandom random = new SecureRandom();
    private static final Set<String> stashUniqueNumbers = new HashSet<>();

    public static String generateUniqueNumber() {
        while (true) {
            StringBuilder sb = new StringBuilder(NUMBER_LENGTH);

            for (int i = 0; i < NUMBER_LENGTH; i++) {
                int index = random.nextInt(DIGITS.length());
                sb.append(DIGITS.charAt(index));
            }
            String number = sb.toString();

            if (!stashUniqueNumbers.contains(number)) {
                stashUniqueNumbers.add(number);
                return number;
            }
        }
    }
}
