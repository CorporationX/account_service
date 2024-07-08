package faang.school.accountservice.repository.cache;

import faang.school.accountservice.model.PaymentHistory;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPaymentHistoryRepository extends KeyValueRepository<PaymentHistory, String> {
}