package faang.school.accountservice.service.account;

import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.exception.TypeNotFoundException;
import faang.school.accountservice.mapper.FreeAccountNumberMapper;
import faang.school.accountservice.model.AccountNumbersSequence;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumberRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
@RequiredArgsConstructor
public class FreeAccountNumberService {
    private final FreeAccountNumberRepository freeAccountNumberRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final FreeAccountNumberMapper freeAccountNumberMapper;
    private final ExecutorService executorService;
    @Value("${free_accounts.numbers.min_quantity}")
    private Integer minAccountsNumbersQuantity;
    @Value("${free_accounts.numbers.generation_of_new.when_min_threshold}")
    private Integer quantityOfNewGeneratesAccountNumbers;


    public BigInteger getFreeNumber(AccountType accountType) {
        if (isNotEnoughFreeAccounts(accountType)) {
            generateNewFreeAccountNumbers(quantityOfNewGeneratesAccountNumbers, accountType);
        }
        Optional<FreeAccountNumber> freeAccountNumber = freeAccountNumberRepository.getFreeAccountNumber(accountType.name());
        if (freeAccountNumber.isEmpty()) {
            freeAccountNumber = Optional.of(generateFreeAccount(accountType));
            generateNewFreeAccountNumbers(minAccountsNumbersQuantity, accountType);
        }
        String accountNumber = freeAccountNumber.get().getAccountNumber().toString();
        return new BigInteger(accountNumber);
    }


    @Transactional
    @Retryable(retryFor = PersistenceException.class, maxAttempts = 5, backoff = @Backoff(delay = 3000, multiplier = 1.5))
    public synchronized FreeAccountNumber generateFreeAccount(AccountType accountType) {
        AccountNumbersSequence accountNumbersSequence = getAccountNumbersSequence(accountType);
        BigInteger currentNumber = accountNumbersSequence.getCurrentNumber().add(BigInteger.ONE);
        accountNumbersSequence.setCurrentNumber(currentNumber);
        String accountNumber = String.format("%s%016d", accountType.getValue(), currentNumber);
        FreeAccountNumber freeAccountNumber = freeAccountNumberMapper.toFreeAccountNumber(accountType, new BigInteger(accountNumber));
        FreeAccountNumber saved = freeAccountNumberRepository.save(freeAccountNumber);
        accountNumbersSequenceRepository.save(accountNumbersSequence);
        log.info("{} was saved to db", freeAccountNumber.getAccountNumber());
        return saved;
    }

    public synchronized BigInteger countFreeAccountNumbersByType(AccountType accountType) {
        return freeAccountNumberRepository.getFreeAccountNumbersCountByType(accountType.name());
    }

    private void generateNewFreeAccountNumbers(int quantity, AccountType accountType) {
        CompletableFuture.runAsync(() -> {
            for (int i = 0; i < quantity; i++) {
                generateFreeAccount(accountType);
            }
            }, executorService)
                .exceptionally(exception -> {
                    log.error("An error occurred while generating new account numbers: " + exception.getMessage());
                    throw new RuntimeException(exception);
                });
    }

    private AccountNumbersSequence getAccountNumbersSequence(AccountType accountType) {
        return accountNumbersSequenceRepository.findByAccountType(accountType.getValue())
                .orElseThrow(() -> new TypeNotFoundException(String.format("Type %s not found!", accountType.name())));
    }

    private boolean isNotEnoughFreeAccounts(AccountType accountType) {
        return freeAccountNumberRepository.getFreeAccountNumbersCountByType(accountType.name()).intValueExact() <= minAccountsNumbersQuantity;
    }

    @PostConstruct
    public void init() {
        for (AccountType accountType : AccountType.values()) {
            generateNewFreeAccountNumbers(quantityOfNewGeneratesAccountNumbers, accountType);
        }
    }
}