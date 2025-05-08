package faang.school.accountservice.service.account;

import faang.school.accountservice.component.AccountNumberGenerator;
import faang.school.accountservice.dto.FreeAccountNumberDto;
import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.exception.DuplicateAccountNumberException;
import faang.school.accountservice.exception.SequenceNotInitializedException;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class FreeAccountNumbersServiceImpl implements FreeAccountNumbersService {
    private final FreeAccountNumbersRepository numbersRepository;
    private final AccountNumbersSequenceRepository sequenceRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    private static final int MAX_RETRIES = 3;

    @Override
    @Transactional
    public FreeAccountNumberDto addFreeAccountNumber(String accountType, String accountNumber) {
        log.info("Adding free account number {} for type {}", accountNumber, accountType);
        FreeAccountNumber.Key key = new FreeAccountNumber.Key(accountType, accountNumber);
        validateNotExists(key);

        FreeAccountNumber entity = new FreeAccountNumber(key, Instant.now());
        numbersRepository.save(entity);
        log.info("addFreeAccountNumber: saved entity {}", entity);

        return new FreeAccountNumberDto(entity.getKey().getAccountType(), entity.getKey().getAccountNumber(),
                entity.getCreatedAt());
    }

    @Override
    @Transactional
    public <R> R withNewAccountNumber(String accountType, Function<String, R> action, String prefix, int totalLength) {
        log.info("withNewAccountNumber: trying free number for type={}", accountType);

        String number = fetchFreeOrGenerateWithRetry(accountType, prefix, totalLength);
        R result = action.apply(number);

        log.debug("withNewAccountNumber: action applied, returning result");
        return result;
    }

    private void validateNotExists(FreeAccountNumber.Key key) {
        if (numbersRepository.existsById(key)) {
            String msg = "Duplicate account number: %s".formatted(key.getAccountNumber());
            log.warn("addFreeAccountNumber: duplicate detected (type={}, number={})",
                    key.getAccountType(), key.getAccountNumber());
            throw new DuplicateAccountNumberException(msg);
        }
    }

    private String fetchFreeOrGenerateWithRetry(String accountType, String prefix, int totalLength) {
        return numbersRepository
                .findFirstByKeyAccountTypeOrderByCreatedAtAsc(accountType)
                .map(this::useFree)
                .orElseGet(() -> {
                    int attempt = 0;
                    while (true) {
                        try {
                            return generateNew(accountType, prefix, totalLength);
                        } catch (OptimisticLockingFailureException e) {
                            if (++attempt >= MAX_RETRIES) {
                                log.error("Optimistic locking failure: attempts exceeded max retries", e);
                                throw e;
                            }
                            log.warn("WithNewAccountNumber: optimistic lock conflict, retrying (attempt {})", attempt);
                        }
                    }
                });
    }

    private String useFree(FreeAccountNumber free) {
        String number = free.getKey().getAccountNumber();
        log.info("withNewAccountNumber: found free number {} (createdAt={})",
                number, free.getCreatedAt());
        numbersRepository.delete(free);
        log.debug("withNewAccountNumber: deleted entity {}", free);
        return number;
    }

    private String generateNew(String accountType, String prefix, int totalLength) {
        log.info("withNewAccountNumber: no free numbers for type={}, will generate new", accountType);

        AccountNumbersSequence seq = findSequence(accountType);
        long next = seq.getCurrentValue() + 1;
        seq.setCurrentValue(next);
        sequenceRepository.saveAndFlush(seq);
        log.info("withNewAccountNumber: sequence incremented to {}", next);

        String number = accountNumberGenerator.generate(prefix, totalLength, next);

        FreeAccountNumber.Key key = new FreeAccountNumber.Key(accountType, number);
        if (numbersRepository.existsById(key)) {
            log.warn("WithNewAccountNumber: generated number {} already exists, will retry", number);
            throw new OptimisticLockingFailureException("Optimistic locking failure");
        }

        log.info("withNewAccountNumber: generated new number {}", number);
        return number;
    }

    private AccountNumbersSequence findSequence(String accountType) {
        return sequenceRepository.findById(accountType)
                .orElseThrow(() -> {
                    log.error("withNewAccountNumber: sequence not initialized for type={}", accountType);
                    return new SequenceNotInitializedException("Sequence not initialized for " + accountType);
                });
    }
}


