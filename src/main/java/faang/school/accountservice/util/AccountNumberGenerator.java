package faang.school.accountservice.util;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class AccountNumberGenerator {
    private final SecureRandom secureRandom;

    public AccountNumberGenerator(
            @Qualifier("accountNumbers") SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    public String generate() {
        int length = secureRandom.nextInt(9) + 12;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(secureRandom.nextInt(10));
        }

        return sb.toString();
    }
}
