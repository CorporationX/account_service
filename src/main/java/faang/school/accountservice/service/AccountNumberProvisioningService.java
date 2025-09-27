package faang.school.accountservice.service;

import faang.school.accountservice.config.context.AccountGenerationConfig;
import faang.school.accountservice.enums.AccountType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.function.Function;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountNumberProvisioningService {

    private final AccountGenerationConfig config;
    private final FreeAccountNumberPoolService poolService;
    private final AccountNumberGenerator generator;
    private final AccountNumberBatchProcessor batchProcessor;

    @Transactional
    public String provideAccountNumber(AccountType type) {
        Optional<String> pooledNumber = poolService.retrieveFromPool(type);
        if (pooledNumber.isPresent()) {
            log.debug("Retrieved number from pool for type {}", type);
            return pooledNumber.get();
        }

        log.info("Pool empty for type {}, generating new number", type);
        return generator.generateAccountNumber(type);
    }

    @Transactional
    public void ensurePoolCapacity(AccountType type, int minCount) {
        long currentCount = poolService.countAvailable(type);

        if (currentCount < minCount) {
            int toGenerate = (int) (minCount - currentCount);
            log.info("Replenishing pool for type {}: generating {} numbers",
                    type, toGenerate);
            batchProcessor.generateAndAddToPool(type, toGenerate);
        }
    }

    @Scheduled(fixedDelayString = "${account.generation.pool-check-interval:3600000}")
    public void maintainPoolLevels() {
        for (AccountType type : AccountType.values()) {
            try {
                ensurePoolCapacity(type, config.getMinPoolSize());
            } catch (Exception e) {
                log.error("Failed to maintain pool for type {}: {}",
                        type, e.getMessage());
            }
        }
    }

    @Transactional
    public <T> T executeWithAccountNumber(AccountType type,
                                          Function<String, T> operation) {
        String accountNumber = provideAccountNumber(type);

        try {
            return operation.apply(accountNumber);
        } catch (Exception e) {
            log.error("Operation failed with number {} for type {}: {}",
                    accountNumber, type, e.getMessage());
            throw e;
        }
    }
}