package faang.school.accountservice.util;

import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountNumberGenerator {

    private final AccountRepository accountRepository;
    private final SecureRandom random = new SecureRandom();

    private static final int ACCOUNT_NUMBER_LENGTH = 20;
    private static final int MAX_ATTEMPTS = 100;

    public String generate() {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            String accountNumber = generateRandomNumber();

            if (!accountRepository.findByAccountNumber(accountNumber).isPresent()) {
                log.debug("Generated unique account number: {} on attempt {}",
                        accountNumber, attempt);
                return accountNumber;
            }

            log.warn("Account number collision detected on attempt {}: {}",
                    attempt, accountNumber);
        }

        log.error("Failed to generate unique account number after {} attempts", MAX_ATTEMPTS);
        throw new RuntimeException("Failed to generate unique account number after "
                + MAX_ATTEMPTS + " attempts. Please contact support.");
    }

    public boolean exists(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).isPresent();
    }

    private String generateRandomNumber() {
        StringBuilder sb = new StringBuilder(ACCOUNT_NUMBER_LENGTH);

        // Первая цифра не может быть 0
        sb.append(random.nextInt(9) + 1);

        // Остальные цифры
        for (int i = 1; i < ACCOUNT_NUMBER_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }
}