package faang.school.accountservice.config.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Slf4j
@Configuration
public class KafkaConfig {
//
//    @Bean
//    public JsonSerializer<Object> jsonSerializer() {
//        log.info("Serializer is create");
//        return new JsonSerializer<>();
//    }
//
//    @Bean
//    public JsonDeserializer<Object> jsonDeserializer() {
//        JsonDeserializer<Object> deserializer = new JsonDeserializer<>();
//        deserializer.addTrustedPackages("*"); // Разрешить все пакеты (можно ограничить при желании)
//        deserializer.setRemoveTypeHeaders(false); // Оставляем заголовки типов, если они есть
//        deserializer.setUseTypeMapperForKey(false); // если ключи сложные, иначе можно false
//        return deserializer;
//    }
}
