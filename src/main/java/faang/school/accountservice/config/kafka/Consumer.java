package faang.school.accountservice.config.kafka;

import lombok.Data;

@Data
public class Consumer {
    private boolean enableAutoCommit;
    private String isolationLevel;
}
