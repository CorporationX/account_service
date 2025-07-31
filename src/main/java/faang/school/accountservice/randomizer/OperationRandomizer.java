package faang.school.accountservice.randomizer;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OperationRandomizer {
    public Long randomOperationId() {
        SecureRandom secureRandom = new SecureRandom();
        return (secureRandom.nextLong());
    }
}
