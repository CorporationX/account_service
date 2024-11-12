package faang.school.accountservice.service;

import faang.school.accountservice.dto.PendingDto;

public interface PaymentService {

    void paymentAuthorization(PendingDto pendingDto);

    void clearPayment(PendingDto pendingDto);
}
