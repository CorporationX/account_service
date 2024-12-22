package faang.school.accountservice.config.context.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumer {

    @KafkaListener(topics = "payment", groupId = "my_consumer_group")
    public void consume(String message) {
        log.info("Consuming message: {}", message);
        System.out.println("Received message: " + message);
    }
}
