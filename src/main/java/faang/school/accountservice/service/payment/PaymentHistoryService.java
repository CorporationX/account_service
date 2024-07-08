package faang.school.accountservice.service.payment;

import faang.school.accountservice.dto.event.PaymentEvent;
import faang.school.accountservice.model.PaymentHistory;

public interface PaymentHistoryService {
    PaymentHistory save(PaymentEvent paymentEvent);
    boolean existsByIdempotencyKey(String idempotencyKey);
}