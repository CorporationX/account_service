package faang.school.accountservice.service.payment;

import faang.school.accountservice.dto.event.PaymentEvent;
import faang.school.accountservice.mapper.PaymentHistoryMapper;
import faang.school.accountservice.mapper.PaymentKeyGenerator;
import faang.school.accountservice.model.PaymentHistory;
import faang.school.accountservice.repository.PaymentHistoryRepository;
import faang.school.accountservice.repository.cache.RedisPaymentHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@CacheConfig(cacheNames = "paymentsHistoryCache")
public class PaymentHistoryServiceImpl implements PaymentHistoryService {
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentHistoryMapper paymentHistoryMapper;
    private final PaymentKeyGenerator paymentKeyGenerator;
    private final RedisPaymentHistoryRepository redisPaymentHistoryRepository;

    @Transactional
    @Cacheable(value = "paymentHistories", key = "#idempotencyKey")
    @Override
    public PaymentHistory save(PaymentEvent paymentEvent) {
        String idempotencyKey = paymentKeyGenerator.generateKey(paymentEvent).toString();
        PaymentHistory paymentHistory = paymentHistoryMapper.toPaymentHistory(paymentEvent);
        paymentHistory.setIdempotencyKey(idempotencyKey);
        PaymentHistory savedPaymentHistory = paymentHistoryRepository.save(paymentHistory);
        redisPaymentHistoryRepository.save(savedPaymentHistory);
        return savedPaymentHistory;
    }

    @Override
    public boolean existsByIdempotencyKey(String idempotencyKey) {
        return redisPaymentHistoryRepository.existsById(idempotencyKey) ||
                paymentHistoryRepository.existsByIdempotencyKey(idempotencyKey);
    }
}