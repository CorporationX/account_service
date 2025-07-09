package faang.school.accountservice.service;


import faang.school.accountservice.dto.event.request.AuthorizationRequestEvent;
import faang.school.accountservice.dto.event.request.CancellationRequestEvent;
import faang.school.accountservice.dto.event.request.ClearRequestEvent;
import faang.school.accountservice.entity.BalanceTransfer;
import faang.school.accountservice.exception.transfer.TransferException;
import faang.school.accountservice.validator.TransferValidator;
import faang.school.accountservice.validator.account.AccountValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {
    private final BalanceService balanceService;
    private final BalanceTransferService balanceTransferService;
    private final TransferValidator transferValidator;
    private final AccountValidator accountValidator;

    @Transactional
    public BalanceTransfer authorizeTransfer(AuthorizationRequestEvent event) throws TransferException {
        log.info("Start authorize transaction {}", event.getAuthorizationId());

        BalanceTransfer newBalanceTransfer = null;
        try {
            accountValidator.validateAccountExistsAndActive(event.getSourceId());
            accountValidator.validateAccountExistsAndActive(event.getTargetId());

            accountValidator.validateAccountCurrency(event.getSourceId(), event.getCurrency());
            accountValidator.validateAccountCurrency(event.getTargetId(), event.getCurrency());

            newBalanceTransfer = balanceTransferService.createBalanceTransfer(event);

            accountValidator.validateAccountOwner(newBalanceTransfer.getSourceAccount(), event.getUserId());

            log.debug("Authorize transfer for account: {} - {}", event.getAuthorizationId(), event.getSourceId());
            balanceService.authorize(event.getSourceId(), event.getAmount());

            balanceTransferService.saveWithAuthorizedStage(newBalanceTransfer);

        } catch (Exception exception) {
            log.error("Authorization {} failed", event.getAuthorizationId(), exception);

            if (newBalanceTransfer != null) {
                balanceTransferService.saveWithAuthorizationFailedStage(newBalanceTransfer);
            }

            throw new TransferException(exception);
        }

        return newBalanceTransfer;
    }

    @Transactional
    public BalanceTransfer clearTransfer(ClearRequestEvent event) throws TransferException {
        log.info("Start clearing transaction {}", event.getTransactionId());

        BalanceTransfer balanceTransfer = null;
        try {
            balanceTransfer = balanceTransferService.getBalanceTransferById(event.getTransactionId());

            transferValidator.validateTransferFinished(balanceTransfer);
            transferValidator.validateTransferFinishAllowed(balanceTransfer);

            log.debug("Clear transfer: {}", event.getTransactionId());
            balanceService.clear(balanceTransfer.getSourceAccount().getId(), balanceTransfer.getAmount());

            log.debug("Deposit transfer: {}", event.getTransactionId());
            balanceService.deposit(balanceTransfer.getTargetAccount().getId(), balanceTransfer.getAmount());

            balanceTransfer = balanceTransferService.saveWithClearedStage(balanceTransfer, event.getInitiator());
        } catch (Exception exception) {
            log.error("Clearing failed", exception);

            if (Objects.nonNull(balanceTransfer)) {
                balanceTransferService.saveWithClearFailedStage(balanceTransfer, event.getInitiator());
            }

            throw new TransferException(exception);
        }

        return balanceTransfer;
    }

    public BalanceTransfer cancelAuthorizationTransfer(CancellationRequestEvent event) throws TransferException {
        log.info("Start cancelling transaction {}", event.getTransactionId());

        BalanceTransfer balanceTransfer = null;
        try {
            balanceTransfer = balanceTransferService.getBalanceTransferById(event.getTransactionId());

            transferValidator.validateTransferFinished(balanceTransfer);
            transferValidator.validateTransferFinishAllowed(balanceTransfer);

            log.debug("Canceling transfer {}", event.getTransactionId());
            balanceService.cancelAuthorization(balanceTransfer.getSourceAccount().getId(), balanceTransfer.getAmount());

            log.debug("Authorization for account {} for funds {} is canceled",
                    balanceTransfer.getSourceAccount().getId(),
                    balanceTransfer.getAmount()
            );

            balanceTransfer = balanceTransferService.saveWithCanceledStage(balanceTransfer);
        } catch (Exception exception) {
            log.error("Cancellation for transaction {} failed", event.getTransactionId(), exception);

            if (balanceTransfer != null) {
                balanceTransferService.saveWithCancellationFailedStage(balanceTransfer);
            }

            throw new TransferException(exception);
        }
        return balanceTransfer;
    }
}