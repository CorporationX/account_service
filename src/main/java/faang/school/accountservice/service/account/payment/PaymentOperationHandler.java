package faang.school.accountservice.service.account.payment;

import faang.school.accountservice.dto.PaymentEventDto;
import faang.school.accountservice.enums.OperationType;
import faang.school.accountservice.service.account.BalanceService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class PaymentOperationHandler {
    protected final PaymentOperationService paymentOperationService;
    protected final BalanceService balanceService;

    public abstract void handlePaymentOperation(PaymentEventDto paymentEventDto);

    public abstract OperationType getRequiredOperationType();
}
