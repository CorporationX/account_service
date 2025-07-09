package faang.school.accountservice.validator;

import faang.school.accountservice.entity.BalanceTransfer;
import faang.school.accountservice.enums.TransferStage;
import faang.school.accountservice.enums.TransferStatus;
import faang.school.accountservice.exception.common.PreConditionFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferValidator {

    public void validateTransferFinished(BalanceTransfer balanceTransfer) {
        boolean isTransferFinished = balanceTransfer.getTransferStatus() == TransferStatus.FINISHED;

        if (isTransferFinished) {
            log.error("Transfer with id {} already finished!", balanceTransfer.getId());
            throw new PreConditionFailedException("Transfer with id %s already finished!".formatted(balanceTransfer.getId()));
        }
    }

    public void validateTransferFinishAllowed(BalanceTransfer balanceTransfer) {
        boolean isTransferNotAuthorized = balanceTransfer.getTransferStage() != TransferStage.AUTHORIZED;
        boolean isTransferNotClearFailed = balanceTransfer.getTransferStage() != TransferStage.CLEAR_FAILED;

        if (isTransferNotAuthorized && isTransferNotClearFailed) {
            log.error("Operation not allowed for transfer {}. Current stage: {}", balanceTransfer.getId(), balanceTransfer.getTransferStage());
            throw new PreConditionFailedException("Operation not allowed for transfer %s. Current stage: %s"
                    .formatted(balanceTransfer.getId(), balanceTransfer.getTransferStage()));
        }
    }
}