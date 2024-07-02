package faang.school.accountservice.service.account.payment;

import faang.school.accountservice.dto.PaymentEventDto;
import faang.school.accountservice.entity.PaymentOperation;
import faang.school.accountservice.enums.OperationType;
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
    public void handlePaymentOperation(PaymentEventDto paymentEventDto) {
        PaymentOperation pendingOperation = paymentOperationService.findById(paymentEventDto.getPaymentNumber());

        moveFoundsFromDebitToCreditAccount(paymentEventDto, pendingOperation);

        pendingOperation.setIsCleared(true);
        pendingOperation.setType(OperationType.CONFIRMATION);

        paymentOperationService.saveOperation(pendingOperation);
    }

    private void moveFoundsFromDebitToCreditAccount(PaymentEventDto paymentEventDto, PaymentOperation pendingOperation) {
        Long debitAccountId = pendingOperation.getDebitAccount().getId();
        Long creditAccountId = pendingOperation.getCreditAccount().getId();

        balanceService.releasePaymentAmount(debitAccountId, pendingOperation.getAmount());
        balanceService.withdrawFromBalance(debitAccountId, paymentEventDto.getAmount());
        balanceService.depositToBalance(creditAccountId, paymentEventDto.getAmount());
    }

    @Override
    public OperationType getRequiredOperationType() {
        return OperationType.CONFIRMATION;
    }
}
