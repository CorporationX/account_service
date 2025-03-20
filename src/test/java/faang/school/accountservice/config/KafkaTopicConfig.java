package faang.school.accountservice.config;

import faang.school.accountservice.BaseIntegrationTest;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.List;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(Map.of(
                "bootstrap.servers", BaseIntegrationTest.KAFKA_CONTAINER.getBootstrapServers()
        ));
    }

    @Bean
    public List<NewTopic> topics() {
        return List.of(
                new NewTopic("dlq-transfer", 1, (short) 1),
                new NewTopic("transfer-request", 1, (short) 1),
                new NewTopic("transfer-response", 1, (short) 1),
                new NewTopic("transfer-cancel-request", 1, (short) 1),
                new NewTopic("transfer-cancel-response", 1, (short) 1),
                new NewTopic("transfer-clearing-request", 1, (short) 1),
                new NewTopic("transfer-clearing-response", 1, (short) 1)
        );
    }
}

