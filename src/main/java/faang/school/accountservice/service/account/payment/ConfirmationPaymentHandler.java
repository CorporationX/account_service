package faang.school.accountservice.service.account.payment;

import faang.school.accountservice.dto.PaymentOperationDto;
import faang.school.accountservice.entity.PaymentOperation;
import faang.school.accountservice.enums.OperationStatus;
import faang.school.accountservice.service.account.BalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class ConfirmationPaymentHandler extends PaymentOperationHandler {

    public ConfirmationPaymentHandler(PaymentOperationService paymentOperationService, BalanceService balanceService) {
        super(paymentOperationService, balanceService);
    }

    @Transactional
    @Override
    public void handlePaymentOperation(PaymentOperationDto clearingOperationDto) {
        PaymentOperation pendingOperation = paymentOperationService.findById(clearingOperationDto.getPaymentId());

        moveFoundsFromDebitToCreditAccount(clearingOperationDto, pendingOperation);

        pendingOperation.setStatus(OperationStatus.CONFIRMED);
        paymentOperationService.saveOperation(pendingOperation);
        log.info("Operation with id {} was confirmed", pendingOperation.getId());
    }

    private void moveFoundsFromDebitToCreditAccount(PaymentOperationDto paymentOperationDto, PaymentOperation pendingOperation) {
        Long debitAccountId = pendingOperation.getSenderAccount().getId();
        Long creditAccountId = pendingOperation.getReceiverAccount().getId();

        balanceService.releasePaymentAmount(debitAccountId, pendingOperation.getAmount());
        balanceService.withdrawFromBalance(debitAccountId, paymentOperationDto.getAmount());
        balanceService.depositToBalance(creditAccountId, paymentOperationDto.getAmount());
    }

    @Override
    public OperationStatus getRequiredOperationStatus() {
        return OperationStatus.CONFIRMATION;
    }
}
