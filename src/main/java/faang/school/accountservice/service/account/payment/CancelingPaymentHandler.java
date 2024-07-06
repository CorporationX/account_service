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
public class CancelingPaymentHandler extends PaymentOperationHandler {

    public CancelingPaymentHandler(PaymentOperationService paymentOperationService, BalanceService balanceService) {
        super(paymentOperationService, balanceService);
    }

    @Override
    @Transactional
    public void handlePaymentOperation(PaymentOperationDto paymentOperationDto) {
        PaymentOperation pendingOperation = paymentOperationService.findById(paymentOperationDto.getPaymentId());

        balanceService.releasePaymentAmount(pendingOperation.getSenderAccount().getId(), pendingOperation.getAmount());

        pendingOperation.setStatus(OperationStatus.CANCELED);
        paymentOperationService.saveOperation(pendingOperation);
        log.info("Operation with id {} was canceled", pendingOperation.getId());
    }

    @Override
    public OperationStatus getRequiredOperationStatus() {
        return OperationStatus.CANCELING;
    }
}
