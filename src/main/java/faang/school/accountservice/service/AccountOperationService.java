package faang.school.accountservice.service;

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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountOperationService {
    private final AccountOperationRepository accountOperationRepository;
    private final AccountOperationMapper accountOperationMapper;
    private final BalanceService balanceService;

    @Transactional
    public void processAuthorization(@NotNull @Valid AuthorizationMessage message) {
        if (accountOperationRepository.existsByPaymentOperationId(message.getOperationId())) {
            log.debug("Authorization has already been processed.");
            return;
        }

        AccountOperation operation =
                accountOperationMapper.authMessageToAccountOperation(message);
        try {
            operation.setOperationStatus(OperationStatus.COMPLETED);

            balanceService.reserveFounds(operation);

            accountOperationRepository.save(operation);
        } catch (Exception e) {
            operation.setOperationStatus(OperationStatus.FAILED);
            operation.setErrorMessage(e.getMessage());

            accountOperationRepository.save(operation);
        }
    }

    @Transactional
    public void processClearing(@NotNull @Valid ClearingMessage message) {
        UUID operationId = message.getOperationId();

        AccountOperation authOperation = accountOperationRepository
                .findAuthOperation(message.getAuthorizationId(), OperationType.AUTHORIZATION, OperationStatus.COMPLETED)
                .orElseThrow(() -> new OperationNotFound("The operation has not found."));

        if (accountOperationRepository
                .existsByPaymentOperationIdAndOperationType(operationId, OperationType.CLEARING)) {
            log.debug("The operation with id {} has already been processed.", operationId);
        }

        AccountOperation operation =
                accountOperationMapper.cloneOperation(authOperation, operationId);
        try {
            operation.setOperationType(OperationType.CLEARING);

            operation = accountOperationRepository.save(operation);

            balanceService.clearBalance(operation);
        } catch (Exception e) {
            operation.setOperationType(OperationType.CLEARING);
            operation.setOperationStatus(OperationStatus.FAILED);
            operation.setErrorMessage(e.getMessage());

            accountOperationRepository.save(operation);
        }
    }

    @Transactional
    public void processCancellation(@NotNull @Valid CancellationMessage message) {
        UUID operationId = message.getOperationId();

        AccountOperation authOperation = accountOperationRepository
                .findAuthOperation(message.getAuthorizationId(), OperationType.AUTHORIZATION, OperationStatus.COMPLETED)
                .orElseThrow(() -> new OperationNotFound("The operation has not found."));

        if (accountOperationRepository.existsByPaymentOperationIdAndOperationType(operationId, OperationType.CANCELLATION)) {
            log.debug("The operation with id {} has already been processed.", operationId);
        }

        AccountOperation operation =
                accountOperationMapper.cloneOperation(authOperation, operationId);
        try {
            operation.setOperationType(OperationType.CANCELLATION);
            balanceService.cancelBalance(operation);

            accountOperationRepository.save(operation);
        } catch (Exception e) {
            operation.setOperationType(OperationType.CANCELLATION);
            operation.setOperationStatus(OperationStatus.FAILED);
            operation.setErrorMessage(e.getMessage());

            accountOperationRepository.save(operation);
        }
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
