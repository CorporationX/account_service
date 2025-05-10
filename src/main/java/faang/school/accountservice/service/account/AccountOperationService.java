package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.AccountOperationResponse;
import faang.school.accountservice.dto.OperationStatus;
import faang.school.accountservice.dto.OperationType;
import faang.school.accountservice.dto.message.AuthorizationMessage;
import faang.school.accountservice.dto.message.CancellationMessage;
import faang.school.accountservice.dto.message.ClearingMessage;
import faang.school.accountservice.exception.OperationNotFound;
import faang.school.accountservice.mapper.AccountOperationMapper;
import faang.school.accountservice.model.AccountOperation;
import faang.school.accountservice.repository.AccountOperationRepository;
import faang.school.accountservice.service.balance.BalanceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Сервис для обработки операций по счетам.
 * Обеспечивает выполнение операций авторизации, клиринга и отмены платежей,
 * а также управление их статусами и взаимодействие с сервисом балансов.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountOperationService {
    private final AccountOperationRepository accountOperationRepository;
    private final AccountOperationMapper accountOperationMapper;
    private final BalanceService balanceService;
    private final OperationProcessor operationProcessor;

    @Transactional
    public void processAuthorization(@NotNull @Valid AuthorizationMessage message) {
        UUID operationId = message.getOperationId();
        if (accountOperationRepository.existsByPaymentOperationId(operationId)) {
            log.debug("Authorization operation with id {} has already been processed.", message.getOperationId());
            return;
        }

        AccountOperation operation =
                accountOperationMapper.authMessageToAccountOperation(message);

        operationProcessor.processOperation(operation,
                operationId,
                OperationType.AUTHORIZATION,
                () -> balanceService.reserveFounds(operation));
    }

    @Transactional
    public void processClearing(@NotNull @Valid ClearingMessage message) {
        UUID operationId = message.getOperationId();

        AccountOperation authOperation = accountOperationRepository
                .findAuthOperation(message.getAuthorizationId(), OperationType.AUTHORIZATION, OperationStatus.COMPLETED)
                .orElseThrow(() -> new OperationNotFound("The operation has not found."));

        if (accountOperationRepository
                .existsByPaymentOperationIdAndOperationType(operationId, OperationType.CLEARING)) {
            log.debug("Clearing operation with id {} has already been processed.", operationId);
            return;
        }

        AccountOperation operation =
                accountOperationMapper.cloneOperation(authOperation, operationId);

        operationProcessor.processOperation(operation,
                operationId,
                OperationType.CLEARING,
                () -> balanceService.clearBalance(operation));
    }

    @Transactional
    public void processCancellation(@NotNull @Valid CancellationMessage message) {
        UUID operationId = message.getOperationId();

        AccountOperation authOperation = accountOperationRepository
                .findAuthOperation(message.getAuthorizationId(), OperationType.AUTHORIZATION, OperationStatus.COMPLETED)
                .orElseThrow(() -> new OperationNotFound("The operation has not found."));

        if (accountOperationRepository.
                existsByPaymentOperationIdAndOperationType(operationId, OperationType.CANCELLATION)) {
            log.debug("Cancel operation with id {} has already been processed.", operationId);
            return;

        }

        AccountOperation operation =
                accountOperationMapper.cloneOperation(authOperation, operationId);

        operationProcessor.processOperation(operation,
                operationId,
                OperationType.CANCELLATION,
                () -> balanceService.cancelBalance(operation));
    }

    public AccountOperationResponse getOperation(@NotNull UUID operationId) {
        AccountOperation operation = accountOperationRepository.findById(operationId)
                .orElseThrow(() -> new OperationNotFound("The operation has not found."));

        UUID id = operation.getAuthorizationId();
        OperationStatus status = operation.getOperationStatus();
        OperationType type = operation.getOperationType();
        String message = operation.getErrorMessage();

        return new AccountOperationResponse(id, status, type, message);
    }
}
