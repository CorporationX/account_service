package faang.school.accountservice.service.account.payment;

import faang.school.accountservice.dto.PaymentEventDto;
import faang.school.accountservice.enums.OperationType;

public interface PaymentOperationHandler {
    void handlePaymentOperation(PaymentEventDto paymentEventDto);

    OperationType getRequiredOperationType();
}
