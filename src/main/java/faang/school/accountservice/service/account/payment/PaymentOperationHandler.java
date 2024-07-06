package faang.school.accountservice.service.account.payment;

import faang.school.accountservice.dto.PaymentOperationDto;
import faang.school.accountservice.enums.OperationStatus;
import faang.school.accountservice.service.account.BalanceService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class PaymentOperationHandler {
    protected final PaymentOperationService paymentOperationService;
    protected final BalanceService balanceService;

    public abstract void handlePaymentOperation(PaymentOperationDto clearingOperationDto);

    public abstract OperationStatus getRequiredOperationStatus();
}
