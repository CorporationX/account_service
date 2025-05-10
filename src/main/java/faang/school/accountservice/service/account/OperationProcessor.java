package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.OperationStatus;
import faang.school.accountservice.dto.OperationType;
import faang.school.accountservice.model.AccountOperation;
import faang.school.accountservice.repository.AccountOperationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Класс для ретрая операций в AccountOperationService
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OperationProcessor {

    private final AccountOperationRepository accountOperationRepository;

    @Retryable(
            retryFor = {RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void processOperation(AccountOperation operation,
                                 UUID operationId,
                                 OperationType operationType,
                                 Runnable balanceOperation) {
        operation.setOperationType(operationType);
        operation.setOperationStatus(OperationStatus.COMPLETED);
        accountOperationRepository.save(operation);
        balanceOperation.run();
    }

    @Recover
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recoverOperationFailure(Exception e,
                                        AccountOperation operation,
                                        UUID operationId,
                                        OperationType operationType,
                                        Runnable balanceOperation) {
        log.error("{} operation {} has failed with exception {}", operationType, operationId, e.getMessage());
        operation.setOperationStatus(OperationStatus.FAILED);
        operation.setErrorMessage(e.getMessage());
        accountOperationRepository.save(operation);
        throw new RuntimeException("Operation failed after retries: " + e.getMessage(), e);
    }
}