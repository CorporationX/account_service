package faang.school.accountservice.service.payment;

import faang.school.accountservice.dto.PaymentOperationDto;

public interface PaymentOperationStrategy {
    void process(PaymentOperationDto payment, String correlationId);
}