package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.OperationStatus;
import faang.school.accountservice.model.cashback.Operation;
import faang.school.accountservice.repository.cashback.OperationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FailedOperationsService {
    private final OperationRepository operationRepository;

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final String FAILED_STATUS = "FAILED";
    private static final String ERROR_STATUS = "ERROR";

    @Transactional(readOnly = true)
    public List<Operation> getFailedOperations(LocalDateTime since) {
        return operationRepository.findByStatusInAndCreatedAtAfter(
                List.of(FAILED_STATUS, ERROR_STATUS),
                since
        );
    }

    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = MAX_RETRY_ATTEMPTS,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Transactional
    public void retryFailedOperation(Operation operation) {
        try {
            processOperation(operation);
            operation.setCashbackProcessed(true);
            operation.setCashbackProcessedAt(LocalDateTime.now());
            operation.setRetryCount(operation.getRetryCount() + 1);
            operation.setLastRetryAt(LocalDateTime.now());
            operation.setStatus(OperationStatus.valueOf("COMPLETED"));
            operationRepository.save(operation);
            log.info("Successfully processed failed operation: {}", operation.getId());
        } catch (Exception e) {
            handleOperationError(operation, e);
            throw e;
        }
    }

    private void processOperation(Operation operation) {
        log.debug("Processing operation: {}", operation.getId());
    }

    private void handleOperationError(Operation operation, Exception e) {
        operation.setStatus(OperationStatus.valueOf(operation.getRetryCount() >= MAX_RETRY_ATTEMPTS ? ERROR_STATUS : FAILED_STATUS));
        operation.setErrorMessage(e.getMessage());
        operation.setLastRetryAt(LocalDateTime.now());
        operation.setRetryCount(operation.getRetryCount() + 1);
        operationRepository.save(operation);
        log.error("Failed to process operation {}: {}", operation.getId(), e.getMessage(), e);
    }
}