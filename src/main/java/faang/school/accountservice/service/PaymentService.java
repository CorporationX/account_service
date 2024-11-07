package faang.school.accountservice.service;

import faang.school.accountservice.dto.PendingDto;

public interface PaymentService {

    PendingDto cancelPayment(PendingDto pendingDto);

    PendingDto clearingPayment(PendingDto pendingDto);
}
