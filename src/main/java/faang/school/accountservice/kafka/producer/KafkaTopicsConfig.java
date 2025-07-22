package faang.school.accountservice.kafka.producer;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicsConfig {
    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        KafkaAdmin admin = new KafkaAdmin(configs);
        admin.setAutoCreate(true);
        return admin;
    }

    @Bean
    public NewTopic requestEventsTopic(
            @Value("${spring.kafka.topics.request.name}") String name,
            @Value("${spring.kafka.topics.request.partitions}") int partitions,
            @Value("${spring.kafka.topics.request.replication-factor}") short replicas
    ) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic requestEventsTopicDlt(
            @Value("${spring.kafka.topics.request.dlt.name}") String name,
            @Value("${spring.kafka.topics.request.dlt.partitions}") int partitions,
            @Value("${spring.kafka.topics.request.dlt.replication-factor}") short replicas
    ) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
