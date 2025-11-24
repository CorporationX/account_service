package faang.school.accountservice.util;

import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {

    private final AccountRepository accountRepository;
    private final SecureRandom random = new SecureRandom();

    private static final int ACCOUNT_NUMBER_LENGTH = 20;
    private static final int MAX_ATTEMPTS = 10;

    public String generate() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String accountNumber = generateRandomNumber();

            if (!accountRepository.findByAccountNumber(accountNumber).isPresent()) {
                return accountNumber;
            }
        }

        throw new RuntimeException("Failed to generate unique account number after "
                + MAX_ATTEMPTS + " attempts");
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
