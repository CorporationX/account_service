package faang.school.accountservice.service.payment;

import faang.school.accountservice.enums.PaymentOperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentOperationStrategyFactory {
    private final Map<PaymentOperationType, PaymentOperationStrategy> strategies;

    @Autowired
    public PaymentOperationStrategyFactory(
            InitiatePaymentStrategy initiateStrategy,
            CancelPaymentStrategy cancelStrategy,
            ConfirmPaymentStrategy confirmStrategy,
            TimeConfirmPaymentStrategy timeConfirmStrategy) {
        strategies = Map.of(
                PaymentOperationType.INITIATE, initiateStrategy,
                PaymentOperationType.CANCEL, cancelStrategy,
                PaymentOperationType.CONFIRM, confirmStrategy,
                PaymentOperationType.TIMECONFIRM, timeConfirmStrategy
        );
    }

    public PaymentOperationStrategy getStrategy(String correlationId, PaymentOperationType type) {
        PaymentOperationStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported payment operation type: " + type + " with payment: " + correlationId);
        }
        return strategy;
    }
}