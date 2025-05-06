package faang.school.accountservice.util;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {
    private final AccountRepository accountRepository;

    public String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = generateAccountNumber();
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    private String generateAccountNumber() {
        String prefix = "42";
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);
        String random = String.format("%06d", ThreadLocalRandom.current().nextInt(10000000));
        String accountNumber = prefix + timestamp + random;
        return accountNumber.substring(0, Math.min(accountNumber.length(), 20));
    }
}
