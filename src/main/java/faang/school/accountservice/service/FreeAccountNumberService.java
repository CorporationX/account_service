package faang.school.accountservice.service;

import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.exception.ResourceNotAvailableException;
import faang.school.accountservice.exception.TypeNotFoundException;
import faang.school.accountservice.mapper.FreeAccountNumberMapper;
import faang.school.accountservice.model.AccountNumbersSequence;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FreeAccountNumberService {
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final FreeAccountNumberMapper freeAccountNumberMapper;

    @Value("${account.number.length}")
    private int accountNumberLength; //16
    @Value("${account.type.length}")
    private int typeCodeLength;//4

    @Transactional
    public String getFreeAccountNumber(AccountType accountType) {
        return freeAccountNumbersRepository
                .getFreeAccountNumber(accountType.getCode())
                .orElseGet(() -> createNextNumber(accountType, false).getAccountNumber());
    }

    @Transactional
    public Integer getQuantityFreeAccountNumbersByType(AccountType accountType) {
        return freeAccountNumbersRepository.getFreeAccountNumbersCountByType(accountType.getCode());
    }

    @Transactional
    @Retryable(retryFor = PersistenceException.class,
            maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 2))
    public FreeAccountNumber createNextNumber(AccountType accountType, boolean isSaved) {
        log.info("Trying to create new free account number");
        AccountNumbersSequence accountNumbersSequence = accountNumbersSequenceRepository
                .incrementCounter(accountType.getCode())
                .orElseThrow(() -> new TypeNotFoundException(accountType.name()));
        Long currentNumber = accountNumbersSequence.getCurrentNumber() - 1;
        FreeAccountNumber freeAccountNumber = freeAccountNumberMapper
                .toFreeAccountNumber(accountType, currentNumber, accountNumberLength - typeCodeLength);
        if (isSaved) {
            freeAccountNumbersRepository.save(freeAccountNumber);
        }
        log.info("Free account number was created {}", freeAccountNumber);
        return freeAccountNumber;
    }

    @Recover
    public FreeAccountNumber recover(PersistenceException e, AccountType accountType, boolean isSaved) {
        log.error("Failed to get next number for account type {} after retries: {}, {}", accountType.getCode(), e.getMessage(), isSaved);
        throw new ResourceNotAvailableException("Unable to get next account number after retries", e);
    }
}
