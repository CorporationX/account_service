package faang.school.accountservice.utils;

import java.security.SecureRandom;

public class AccountUtil {
    public static String generateNumber() {
        SecureRandom random = new SecureRandom();
        StringBuilder randomPart = new StringBuilder();
        Long timePart = System.currentTimeMillis();
        randomPart.append(timePart);
        int randomPartLength = random.nextInt(12, 21) - String.valueOf(timePart).length();

        for (int i = 1; i < randomPartLength; i++) {
            randomPart.append(random.nextInt(10));
        }
        return randomPart.toString();
    }
}
