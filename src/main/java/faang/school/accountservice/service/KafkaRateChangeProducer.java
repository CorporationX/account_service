package faang.school.accountservice.service;

import faang.school.accountservice.dto.event.RateChangeNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaRateChangeProducer {

    private final KafkaTemplate<String, RateChangeNotificationEvent> kafkaTemplate;
    private static final String TOPIC = "rate_change_notifications";

    public void sendRateChangeNotification(Long tariffId, String oldRate, String newRate, String date) {
        RateChangeNotificationEvent event = new RateChangeNotificationEvent(tariffId, oldRate, newRate, date);
        kafkaTemplate.send(TOPIC, event);
    }
}