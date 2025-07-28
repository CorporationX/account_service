package faang.school.accountservice.config;

import faang.school.accountservice.dto.CreateRequestDto;
import faang.school.accountservice.dto.dms.PendingRequestDto;
import faang.school.accountservice.dto.dms.ResponseClearingDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${kafka.host}")
    private String host;

    // Общие настройки для всех фабрик
    public Map<String, Object> getCommonConsumerProperties() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, host);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        return props;
    }

    @Bean
    public ConsumerFactory<String, PendingRequestDto> pendingRequestDtoConsumerFactory(){
        Map<String, Object> props = getCommonConsumerProperties();

        JsonDeserializer<PendingRequestDto> deserializer = new JsonDeserializer<>(PendingRequestDto.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PendingRequestDto> pendingRequestKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PendingRequestDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(pendingRequestDtoConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, CreateRequestDto> createRequestDtoConsumerFactory(){
        Map<String, Object> props = getCommonConsumerProperties();

        JsonDeserializer<CreateRequestDto> deserializer = new JsonDeserializer<>(CreateRequestDto.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CreateRequestDto>
    createRequestKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CreateRequestDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createRequestDtoConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, ResponseClearingDto> responseClearingDtoConsumerFactory(){
        Map<String, Object> props = getCommonConsumerProperties();

        JsonDeserializer<ResponseClearingDto> deserializer = new JsonDeserializer<>(ResponseClearingDto.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ResponseClearingDto>
    responseClearingConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ResponseClearingDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(responseClearingDtoConsumerFactory());
        return factory;
    }
}
