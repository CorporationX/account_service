package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.AccountNumberGenerationException;
import faang.school.accountservice.exception.AccountTypeNotInitializedException;
import faang.school.accountservice.exception.UnsupportedAccountTypeException;
import faang.school.accountservice.repository.AccountSequenceRepository;
import faang.school.accountservice.repository.FreeAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreeAccountNumberServiceImpl implements FreeAccountNumberService {

    private static final long DEBIT_ACCOUNT_PREFIX = 4200_0000_0000_0000L;
    private static final long CREDIT_ACCOUNT_PREFIX = 5236_0000_0000_0000L;
    private static final int MAX_RETRY_ATTEMPTS = 10;

    private final FreeAccountRepository freeAccountRepository;
    private final AccountSequenceRepository accountSequenceRepository;

    @Transactional
    @Override
    public void generateAccountNumbers(AccountType type, int batchSize) {

        for (int attempt = 0; attempt < MAX_RETRY_ATTEMPTS; attempt++) {
            AccountNumberSequence sequence = accountSequenceRepository.findByType(type)
                    .orElseThrow(() -> new AccountTypeNotInitializedException(type));

            int updatedRows = accountSequenceRepository.incrementCounter(
                    type,
                    sequence.getCounter(),
                    sequence.getVersion(),
                    batchSize
            );

            if (updatedRows == 1) {
                List<FreeAccountNumber> numbers = new ArrayList<>(batchSize);
                long startCounter = sequence.getCounter();

                for (long i = 0; i < batchSize; i++) {
                    long accountNumber = getAccountPattern(type) + startCounter + i;
                    numbers.add(new FreeAccountNumber(new FreeAccountId(type, accountNumber)
                    ));
                }
                freeAccountRepository.saveAll(numbers);

                log.info("Successfully generated {} account numbers for type: {}", batchSize, type);
                return;
            }

            log.debug("Conflict while generating account numbers, retrying... attempt {}", attempt + 1);
        }

        throw new AccountNumberGenerationException("Failed to generate batch account numbers after "
                + MAX_RETRY_ATTEMPTS + " attempts for type: " + type);
    }

    @Transactional
    @Override
    public Long retrieveAccountNumber(AccountType type) {
        Optional<FreeAccountNumber> freeAccountOpt = freeAccountRepository
                .findFirstByType(type.name());

        if (freeAccountOpt.isPresent()) {
            FreeAccountNumber freeAccount = freeAccountOpt.get();
            Long accountNumber = freeAccount.getId().getAccountNumber();

            freeAccountRepository.delete(freeAccount);

            log.info("Retrieved free account number: {} for type: {}", accountNumber, type);
            return accountNumber;
        }

        log.info("No free account numbers available, generating new one for type: {}", type);
        return generateNewAccountNumber(type);
    }

    private Long generateNewAccountNumber(AccountType type) {

        for (int attempt = 0; attempt < MAX_RETRY_ATTEMPTS; attempt++) {
            AccountNumberSequence sequence = accountSequenceRepository.findByType(type)
                    .orElseThrow(() -> new AccountTypeNotInitializedException(type));

            int updatedRows = accountSequenceRepository.incrementCounter(
                    type,
                    sequence.getCounter(),
                    sequence.getVersion(),
                    1);

            if (updatedRows == 1) {
                long accountNumber = getAccountPattern(type) + sequence.getCounter();
                log.info("Generated new account number on-demand: {} for type: {}", accountNumber, type);
                return accountNumber;
            }

            log.debug("Conflict while generating single account number, retrying... attempt {}", attempt + 1);
        }

        throw new AccountNumberGenerationException("Failed to generate single account number after "
                + MAX_RETRY_ATTEMPTS + " attempts for type: " + type);
    }

    private long getAccountPattern(AccountType type) {
        return switch (type) {
            case DEBIT -> DEBIT_ACCOUNT_PREFIX;
            case CREDIT -> CREDIT_ACCOUNT_PREFIX;
            default -> throw new UnsupportedAccountTypeException(type);
        };
    }
}
