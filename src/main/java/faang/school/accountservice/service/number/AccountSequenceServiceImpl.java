package faang.school.accountservice.service.number;

import faang.school.accountservice.config.AccountNumberProperties;
import faang.school.accountservice.entity.account.AccountSeq;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
    incrementCounte should be called from not transactional service,
    so we move it out to a separate service to correctly trigger
    @Transactional annotation
 */
@Service
@RequiredArgsConstructor
public class AccountSequenceServiceImpl implements AccountSequenceService {

    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final AccountNumberProperties props;

    /*
        Optimistic DB lock counter with retries
     */
    @Override
    @Transactional
    public AccountPeriod incrementCounter(AccountType type, int batchSize) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be > 0");
        }

        int maxRetries = Math.max(1, props.getMaxRetries());

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            var optionalSeq = accountNumbersSequenceRepository.findById(type);

            if (optionalSeq.isEmpty()) {
                accountNumbersSequenceRepository.initIfAbsent(type);
                continue;
            }

            AccountSeq seq = optionalSeq.get();
            long current = seq.getCounter();
            long initial = current + 1;
            long newCounter = current + batchSize;

            int updated = accountNumbersSequenceRepository.tryIncrementCounter(type, current, batchSize);

            if (updated == 1) {
                return new AccountPeriod(initial, newCounter);
            }
        }

        throw new IllegalStateException("Failed to increment counter for: " + type);
    }

}
