package faang.school.accountservice.listener;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RedisTopics {
    PAYMENT_REQUEST("paymentRequest-event");

    private final String topic;
}
