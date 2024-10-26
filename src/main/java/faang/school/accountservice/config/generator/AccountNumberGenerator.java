package faang.school.accountservice.config.generator;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class AccountNumberGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int MIN_LENGTH = 12;
    private static final int MAX_LENGTH = 20;

    public String generateAccountNumber() {
        int length = MIN_LENGTH + RANDOM.nextInt(MAX_LENGTH - MIN_LENGTH + 1);
        StringBuilder accountNumber = new StringBuilder();

        for (int i = 0; i < length; i++) {
            accountNumber.append(RANDOM.nextInt(10));
        }

        return accountNumber.toString();
    }
}
