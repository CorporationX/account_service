package faang.school.accountservice.service.implementations;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.GenerateNumberException;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.interfaces.FreeAccountNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreeAccountNumberServiceImpl implements FreeAccountNumberService {
    private static final String FIXED_PREFIX = "1111222233334444";
    private static final int MAX_ATTEMPTS = 100;
    private static final AtomicInteger sequence = new AtomicInteger(0);

    private final AccountRepository accountRepository;

    @Override
    public String generateAccountNumber(AccountType accountType) {
        int attempts = 0;
        while (attempts < MAX_ATTEMPTS) {
            String lastFourDigits = String.format("%04d", sequence.getAndIncrement() % 10000);
            String accountNumber = FIXED_PREFIX + lastFourDigits;
            if (!accountRepository.existsByAccountNumber(accountNumber)) {
                log.info("Generated unique account number: {}", accountNumber);
                return accountNumber;
            }
            attempts++;
            log.debug("Account number {} already exists, retrying (attempt {}/{})", accountNumber, attempts, MAX_ATTEMPTS);
        }
        throw new GenerateNumberException("Failed to generate unique account number after " + MAX_ATTEMPTS + " attempts");
    }
}
