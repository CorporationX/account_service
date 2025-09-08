package faang.school.accountservice.kafka;

import faang.school.accountservice.model.dto.PaymentMessageDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация Kafka Consumer для AccountBalanceService.
 * <p>
 * Настраивает десериализацию сообщений, фабрику слушателей и режим подтверждения сообщений.
 * Используется для обработки платежных событий (authorization, cancel, clearing).
 * </p>
 */
@Configuration
public class KafkaConsumerConfig {

    /**
     * Адрес Kafka брокеров.
     */
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Группа потребителей Kafka для AccountBalanceService.
     */
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    /**
     * Создает фабрику ConsumerFactory для работы с PaymentMessageDto.
     * <p>
     * Конфигурирует:
     * - Bootstrap сервера
     * - Группу потребителей
     * - Десериализацию ключей и значений
     * - Режим AUTO_OFFSET_RESET для чтения с начала топика при необходимости
     * </p>
     *
     * @return ConsumerFactory<String, PaymentMessageDto>
     */
    @Bean
    public ConsumerFactory<String, PaymentMessageDto> consumerFactory() {
        JsonDeserializer<PaymentMessageDto> deserializer = new JsonDeserializer<>(PaymentMessageDto.class, false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    /**
     * Создает фабрику слушателей Kafka.
     * <p>
     * Настраивает:
     * - ConsumerFactory
     * - Режим подтверждения сообщений {@link ContainerProperties.AckMode#RECORD}, чтобы каждое сообщение подтверждалось отдельно
     * </p>
     *
     * @return ConcurrentKafkaListenerContainerFactory<String, PaymentMessageDto>
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentMessageDto> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentMessageDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
}