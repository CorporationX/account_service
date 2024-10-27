package faang.school.accountservice.listener;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RedisTopics {
    PAYMENT_REQUEST("paymentRequest-event"),
    PAYMENT_RESPONSE("paymentResponse-event"),
    PAYMENT_CLEAR("paymentClear-event");

    private final String topic;
}
