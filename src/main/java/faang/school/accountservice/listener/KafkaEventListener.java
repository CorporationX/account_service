package faang.school.accountservice.listener;

import org.springframework.kafka.support.Acknowledgment;

public interface KafkaEventListener<T> {
    void onEvent(T event, Acknowledgment acknowledgment);
}
