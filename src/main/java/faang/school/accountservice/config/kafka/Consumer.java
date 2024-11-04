package faang.school.accountservice.config.kafka;

import lombok.Data;

@Data
public class Consumer {
    private String keyDeserializer;
    private String valueDeserializer;
    private boolean enableAutoCommit;
    private String isolationLevel;
}
