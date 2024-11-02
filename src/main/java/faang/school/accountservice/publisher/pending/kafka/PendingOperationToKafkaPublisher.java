package faang.school.accountservice.publisher.pending.kafka;

import faang.school.accountservice.dto.balance.response.CheckingAccountBalance;
import faang.school.accountservice.publisher.pending.PendingOperationStatusPublisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(prefix = "app", name = "messaging", havingValue = "kafka")
@Component
public class PendingOperationToKafkaPublisher implements PendingOperationStatusPublisher {
    @Override
    public void publish(CheckingAccountBalance checkingAccountBalance) {

    }
}
