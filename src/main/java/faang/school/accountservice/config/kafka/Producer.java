package faang.school.accountservice.config.kafka;

import lombok.Data;

@Data
public class Producer {
    private String acks;
    private boolean enableIdempotence;
    private int retries;
    private int maxInFlightRequestsPerConnection;
}
