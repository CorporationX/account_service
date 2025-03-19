package faang.school.accountservice.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@ConfigurationProperties (prefix = "spring.kafka.topics")
@Configuration
public class KafkaTopicsProperties {
    private String dlqTransfer;
    private String transferRequest;
    private String transferResponse;
    private String transferCancelRequest;
    private String transferCancelResponse;
    private String transferClearingRequest;
    private String transferClearingResponse;
}
