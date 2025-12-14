package faang.school.accountservice.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class AccountNumberGenerator {
    private static final int MIN_LENGTH = 16;
    private static final int MAX_LENGTH = 20;

    public String generateNumber() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int length = random.nextInt(MIN_LENGTH, MAX_LENGTH + 1);

        StringBuilder stringBuilder = new StringBuilder(length);
        stringBuilder.append(random.nextInt(1, 10));
        for (int i = 1; i < length; i++) {
            stringBuilder.append(random.nextInt(0, 10));
        }
        return stringBuilder.toString();
    }
}
