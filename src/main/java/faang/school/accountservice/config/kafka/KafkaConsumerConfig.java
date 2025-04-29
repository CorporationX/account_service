package faang.school.accountservice.config.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {
    private final KafkaProperties kafkaProperties;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> configs = new HashMap<>(kafkaProperties.getConsumerConfigs());

        // Явно задаём десериализаторы
        configs.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                ErrorHandlingDeserializer.class);
        configs.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                ErrorHandlingDeserializer.class);
        configs.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS,
                StringDeserializer.class);
        configs.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,
                JsonDeserializer.class);

        // Дополнительные настройки для JsonDeserializer
        configs.put(JsonDeserializer.TRUSTED_PACKAGES,
                "faang.school.accountservice.dto.message,faang.school.paymentservice.dto.message");
        configs.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, true);
        configs.put(JsonDeserializer.REMOVE_TYPE_INFO_HEADERS, false);
        configs.put(JsonDeserializer.TYPE_MAPPINGS,
                "faang.school.paymentservice.dto.message.AuthorizationMessage:faang.school.accountservice.dto.message.AuthorizationMessage," +
                        "faang.school.paymentservice.dto.message.CancellationMessage:faang.school.accountservice.dto.message.CancellationMessage," +
                        "faang.school.paymentservice.dto.message.ClearingMessage:faang.school.accountservice.dto.message.ClearingMessage");

        return new DefaultKafkaConsumerFactory<>(configs);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    @Bean
    public CommonErrorHandler errorHandler() {
        return new DefaultErrorHandler((record, exception) -> {
            log.error("Ошибка обработки сообщения: {}, данные: {}", exception.getMessage(), record);
        });
    }
}