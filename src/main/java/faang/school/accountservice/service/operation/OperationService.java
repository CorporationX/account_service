package faang.school.accountservice.service.operation;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.operation.Operation;
import faang.school.accountservice.entity.operation.OperationStatus;
import faang.school.accountservice.entity.operation.OperationType;
import faang.school.accountservice.exception.operation.payment.ConcurrentOperationException;
import faang.school.accountservice.exception.operation.payment.OperationAlreadyExistsException;
import faang.school.accountservice.repository.operation.OperationRepository;
import faang.school.accountservice.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OperationService {
    private final OperationRepository operationRepository;
    private final AccountService accountService;

    // TODO: стутус отдельным методом или нет
    @Transactional
    public Operation createOperation(UUID operationToken, OperationType type, long userId, UUID accountFromId, UUID accountToId, String lock, String storage) {
        try {
            // TODO: исключение
            operationRepository.findByLockedBy(lock).ifPresent(operation -> {
                throw new ConcurrentOperationException("");
            });
            Operation operation = new Operation();
            operation.setOperationToken(operationToken);
            operation.setType(type);
            operation.setUserId(userId);
            operation.setActive(true);
            // TODO: возможно не нужно
            operation.setLockedBy(String.valueOf(userId));
            operation.setStorage(storage);

            Account accountFrom = accountService.getAccountById(accountFromId);
            Account accountTo = accountService.getAccountById(accountToId);
            operation.setAccountFrom(accountFrom);
            operation.setAccountTo(accountTo);
            operation.setStatus(OperationStatus.IN_PROGRESS);

            Operation savedOperation = operationRepository.save(operation);
            log.info("Operation {} has been saved", operation);

            return savedOperation;
            // TODO: нарушение индекса уникальности
        } catch (Exception ex) {
            throw new OperationAlreadyExistsException("");
        }
    }
}
