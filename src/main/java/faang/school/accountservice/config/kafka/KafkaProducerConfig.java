package faang.school.accountservice.config.kafka;

import faang.school.accountservice.dto.deadletter.DeadLetterMessage;
import faang.school.accountservice.dto.event.request.AuthorizationRequestEvent;
import faang.school.accountservice.dto.event.request.CancellationRequestEvent;
import faang.school.accountservice.dto.event.request.ClearRequestEvent;
import faang.school.accountservice.dto.event.responce.failed.AuthorizationFailedEvent;
import faang.school.accountservice.dto.event.responce.failed.CancellationFailedEvent;
import faang.school.accountservice.dto.event.responce.failed.ClearFailedEvent;
import faang.school.accountservice.dto.event.responce.success.AuthorizationSuccessEvent;
import faang.school.accountservice.dto.event.responce.success.CancellationSuccessEvent;
import faang.school.accountservice.dto.event.responce.success.ClearSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaTemplate<String, AuthorizationRequestEvent> authorizeRequest() {
        return new KafkaTemplate<>(jsonProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, ClearRequestEvent> clearRequest() {
        return new KafkaTemplate<>(jsonProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, CancellationRequestEvent> cancelRequest() {
        return new KafkaTemplate<>(jsonProducerFactory());
    }

    //
    @Bean
    public KafkaTemplate<String, AuthorizationSuccessEvent> authorizedEventTemplate() {
        return new KafkaTemplate<>(jsonProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, AuthorizationFailedEvent> authorizationFailedEventTemplate() {
        return new KafkaTemplate<>(jsonProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, ClearSuccessEvent> clearedEventTemplate() {
        return new KafkaTemplate<>(jsonProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, ClearFailedEvent> clearingFailedEventTemplate() {
        return new KafkaTemplate<>(jsonProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, CancellationSuccessEvent> canceledEventTemplate() {
        return new KafkaTemplate<>(jsonProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, CancellationFailedEvent> cancellationFailedEventTemplate() {
        return new KafkaTemplate<>(jsonProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, String> deadLetterTemplate() {
        return new KafkaTemplate<>(jsonProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, DeadLetterMessage> brokenTransfersTemplate() {
        return new KafkaTemplate<>(jsonProducerFactory());
    }

    private <T> ProducerFactory<String, T> jsonProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(config);
    }

    private <T> ProducerFactory<String, T> stringProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(config);
    }
}