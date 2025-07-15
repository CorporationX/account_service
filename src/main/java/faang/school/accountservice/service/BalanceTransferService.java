package faang.school.accountservice.service;


import faang.school.accountservice.dto.event.request.AuthorizationRequestEvent;
import faang.school.accountservice.entity.BalanceTransfer;
import faang.school.accountservice.enums.Initiator;
import faang.school.accountservice.enums.TransferStage;
import faang.school.accountservice.enums.TransferStatus;
import faang.school.accountservice.exception.common.RecordNotFoundException;
import faang.school.accountservice.repository.BalanceTransferRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceTransferService {

    private final AccountService accountService;
    private final BalanceTransferRepository balanceTransferRepository;

    @Transactional(readOnly = true)
    public BalanceTransfer getBalanceTransferById(UUID transferId) {
        return balanceTransferRepository.findById(transferId)
                .orElseThrow(() -> new RecordNotFoundException("Transfer with id %s not found!".formatted(transferId)));
    }

    public BalanceTransfer createBalanceTransfer(AuthorizationRequestEvent event) {
        BalanceTransfer newBalanceTransfer = BalanceTransfer.builder()
                .authorizationId(event.getAuthorizationId())
                .sourceAccount(accountService.getAccountById(event.getSourceId()))
                .targetAccount(accountService.getAccountById(event.getTargetId()))
                .amount(event.getAmount())
                .currency(event.getCurrency())
                .build();

        return balanceTransferRepository.save(newBalanceTransfer);
    }

    public BalanceTransfer saveWithAuthorizedStage(@NotNull BalanceTransfer transfer) {
        transfer.setTransferStage(TransferStage.AUTHORIZED);
        return balanceTransferRepository.save(transfer);
    }

    public BalanceTransfer saveWithAuthorizationFailedStage(@NotNull BalanceTransfer transfer) {
        transfer.setTransferStage(TransferStage.AUTHORIZATION_FAILED);
        transfer.setTransferStatus(TransferStatus.FINISHED);
        return balanceTransferRepository.save(transfer);
    }

    public BalanceTransfer saveWithClearedStage(@NotNull BalanceTransfer transfer, @NotNull Initiator initiator) {
        transfer.setInitiator(initiator);
        transfer.setTransferStage(TransferStage.CLEARED);
        transfer.setTransferStatus(TransferStatus.FINISHED);
        return balanceTransferRepository.save(transfer);
    }

    public BalanceTransfer saveWithClearFailedStage(@NotNull BalanceTransfer transfer, Initiator initiator) {
        transfer.setInitiator(initiator);
        transfer.setTransferStage(TransferStage.CLEAR_FAILED);
        return balanceTransferRepository.save(transfer);
    }

    public BalanceTransfer saveWithCanceledStage(@NotNull BalanceTransfer transfer) {
        transfer.setTransferStage(TransferStage.CANCELED);
        transfer.setTransferStatus(TransferStatus.FINISHED);
        return balanceTransferRepository.save(transfer);
    }

    public BalanceTransfer saveWithCancellationFailedStage(@NotNull BalanceTransfer transfer) {
        transfer.setTransferStage(TransferStage.CANCELLATION_FAILED);
        return balanceTransferRepository.save(transfer);
    }
}