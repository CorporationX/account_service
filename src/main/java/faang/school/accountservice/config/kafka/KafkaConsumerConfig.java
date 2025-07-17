package faang.school.accountservice.config.kafka;

import faang.school.accountservice.dto.event.request.AuthorizationRequestEvent;
import faang.school.accountservice.dto.event.request.CancellationRequestEvent;
import faang.school.accountservice.dto.event.request.ClearRequestEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AuthorizationRequestEvent> kafkaAuthorizationEventListener(
            DefaultErrorHandler errorHandler,
            @Value(value = "${spring.kafka.topics.transfer.consume.authorization-requests-topic.consumer-pool-size}")
            Integer concurrency
    ) {
        return concurrentKafkaListenerJsonFactory(
                AuthorizationRequestEvent.class,
                errorHandler,
                concurrency
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ClearRequestEvent> kafkaClearEventListener(
            DefaultErrorHandler errorHandler,
            @Value(value = "${spring.kafka.topics.transfer.consume.clear-requests-topic.consumer-pool-size}")
            Integer concurrency
    ) {
        return concurrentKafkaListenerJsonFactory(
                ClearRequestEvent.class,
                errorHandler,
                concurrency
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CancellationRequestEvent> kafkaCancellationEventListener(
            DefaultErrorHandler errorHandler,
            @Value(value = "${spring.kafka.topics.transfer.consume.cancellation-requests-topic.consumer-pool-size}")
            Integer concurrency
    ) {
        return concurrentKafkaListenerJsonFactory(
                CancellationRequestEvent.class,
                errorHandler,
                concurrency
        );
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> concurrentKafkaListenerJsonFactory(
            Class<T> tClass,
            DefaultErrorHandler errorHandler,
            Integer concurrency
    ) {
        Map<String, Object> jsonFactoryConfig = new HashMap<>() {{
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
            put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
            put("spring.deserializer.value.delegate.class", JsonDeserializer.class.getName());
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            put(JsonDeserializer.VALUE_DEFAULT_TYPE, tClass.getName());
        }};

        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();

        ConsumerFactory<String, T> consumerFactory = new DefaultKafkaConsumerFactory<>(jsonFactoryConfig);

        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(concurrency);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}