package faang.school.accountservice.publisher.pending;

import faang.school.accountservice.dto.balance.response.CheckingAccountBalance;

public interface PendingOperationStatusPublisher {
    void publish(CheckingAccountBalance checkingAccountBalance);
}
