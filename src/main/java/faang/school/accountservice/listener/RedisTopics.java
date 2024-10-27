package faang.school.accountservice.listener;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RedisTopics {
    PAYMENT_REQUEST("paymentRequest-event"),
    PAYMENT_APPROVE("paymentApprove-event"),
    PAYMENT_CLEAR("paymentClear-event"),
    PAYMENT_CANCEL("paymentCancel-event");

    private final String topic;
}
