package faang.school.accountservice.listener;

import faang.school.accountservice.dto.AuthorizationEvent;
import faang.school.accountservice.message.AuthorizationEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerListener {
    private final AuthorizationEventHandler authorizationEventHandler;

    // todo: для каждого топика нужен свой listener
    @KafkaListener(topics = "authorization-topic", groupId = "my_consumer_group")
    public void consume(ConsumerRecord<String, AuthorizationEvent> record) {
        // Извлечение значения из ConsumerRecord
        AuthorizationEvent event = record.value();
        // Обработка полученного события
        System.out.println("Received AuthorizationEvent: " + event);
        // Здесь можно добавить логику какую либо
        authorizationEventHandler.handle(event);
    }
}
