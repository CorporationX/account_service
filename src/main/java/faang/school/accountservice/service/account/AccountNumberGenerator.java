package faang.school.accountservice.service.account;

import faang.school.accountservice.config.properties.AccountNumberGeneratorProperties;
import faang.school.accountservice.exception.AccountOperationException;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {

    private static final int ACCOUNT_NUMBER_LENGTH = 16;
    private final AccountRepository accountRepository;
    private final AccountNumberGeneratorProperties properties;

    public String generate() {
        int attempts = 0;
        String candidate;

        do {
            candidate = generateUuidBased();
            attempts++;

            if (attempts >= properties.getMaxGenerationAttempts()) {
                throw new AccountOperationException(
                        String.format("Failed to generate unique account number after %d attempts",
                                properties.getMaxGenerationAttempts()));
            }
        } while (accountRepository.existsByNumber(candidate));

        log.debug("Account number generated successfully using UUID after {} attempts", attempts);
        return candidate;
    }

    private String generateUuidBased() {
        UUID uuid = UUID.randomUUID();

        long mostSig = Math.abs(uuid.getMostSignificantBits());
        long leastSig = Math.abs(uuid.getLeastSignificantBits());

        String combined = String.valueOf(mostSig) + String.valueOf(leastSig);

        if (combined.length() >= ACCOUNT_NUMBER_LENGTH) {
            return combined.substring(0, ACCOUNT_NUMBER_LENGTH);
        } else {
            return String.format("%" + ACCOUNT_NUMBER_LENGTH + "s", combined)
                    .replace(' ', '0');
        }
    }
}