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
public class CancelingPaymentHandler extends PaymentOperationHandler {

    public CancelingPaymentHandler(PaymentOperationService paymentOperationService, BalanceService balanceService) {
        super(paymentOperationService, balanceService);
    }

    @Transactional
    @Override
    public void handlePaymentOperation(PaymentEventDto paymentEventDto) {
        PaymentOperation pendingOperation = paymentOperationService.findById(paymentEventDto.getPaymentNumber());

        balanceService.releasePaymentAmount(pendingOperation.getDebitAccount().getId(), pendingOperation.getAmount());

        pendingOperation.setIsCleared(true);
        pendingOperation.setType(OperationType.CANCELING);

        paymentOperationService.saveOperation(pendingOperation);
    }

    @Override
    public OperationType getRequiredOperationType() {
        return OperationType.CANCELING;
    }
}
