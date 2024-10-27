package faang.school.accountservice.service.impl.account.number;

import faang.school.accountservice.model.entity.AccountNumberSequence;
import faang.school.accountservice.model.entity.FreeAccountNumber;
import faang.school.accountservice.model.entity.FreeAccountNumberId;
import faang.school.accountservice.model.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreeAccountNumbersServiceImpl implements FreeAccountNumbersService {

    private final AccountNumbersSequenceRepository sequenceRepository;
    private final FreeAccountNumbersRepository numbersRepository;

    @Override
    @Transactional
    public void getUniqueAccountNumber(Consumer<FreeAccountNumber> accountConsumer, AccountType accountType) {
        FreeAccountNumber freeAccountNumber = numbersRepository.findAndDeleteFirst(accountType)
                .orElseGet(() -> generateUniqueAccountNumber(accountType));

        accountConsumer.accept(freeAccountNumber);
    }

    @Override
    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000L))
    public FreeAccountNumber generateUniqueAccountNumber(AccountType accountType) {
        log.info("Generating unique account number with account type {}", accountType);

        AccountNumberSequence sequence = sequenceRepository.findByAccountType(accountType)
                .orElseGet(() -> {
                    log.info("Creating new sequence with account type {}", accountType);
                    return sequenceRepository.createAndGetSequence(accountType);
                });

        long counter = sequence.getCounter();

        boolean isIncremented = sequenceRepository.isIncremented(accountType, counter);
        if (!isIncremented) {
            throw new OptimisticLockingFailureException("Account with sequence %s is already in use"
                    .formatted(counter));
        }

        String finalNumber = assembleAccountNumber(accountType, counter);

        return new FreeAccountNumber(new FreeAccountNumberId(accountType, finalNumber));
    }

    private String assembleAccountNumber(AccountType accountType, long counter) {
        int accountLength = accountType.getLength();
        String maskStr = String.valueOf(accountType.getMask());
        String sequenceStr = String.valueOf(counter);

        String zeros = "0".repeat(accountLength - maskStr.length() - sequenceStr.length());

        return maskStr.concat(zeros).concat(sequenceStr);
    }
}
