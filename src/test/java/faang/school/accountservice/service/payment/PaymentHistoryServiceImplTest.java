package faang.school.accountservice.service.payment;

import faang.school.accountservice.dto.event.PaymentEvent;
import faang.school.accountservice.mapper.PaymentHistoryMapper;
import faang.school.accountservice.mapper.PaymentKeyGenerator;
import faang.school.accountservice.model.PaymentHistory;
import faang.school.accountservice.repository.PaymentHistoryRepository;
import faang.school.accountservice.repository.cache.RedisPaymentHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentHistoryServiceImplTest {
    private static final UUID IDEMPOTENCY_KEY = UUID.randomUUID();

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;
    @Mock
    private PaymentHistoryMapper paymentHistoryMapper;
    @Mock
    private PaymentKeyGenerator paymentKeyGenerator;
    @Mock
    private RedisPaymentHistoryRepository redisPaymentHistoryRepository;
    @InjectMocks
    private PaymentHistoryServiceImpl paymentHistoryService;

    private PaymentHistory paymentHistory;
    private PaymentEvent paymentEvent;

    @BeforeEach
    void setUp() {
        paymentHistory = PaymentHistory
                .builder()
                .build();
        paymentEvent = PaymentEvent
                .builder()
                .build();
    }

    @Test
    public void whenSaveThenReturnPaymentHistory() {
        when(paymentKeyGenerator.generateKey(paymentEvent)).thenReturn(IDEMPOTENCY_KEY);
        when(paymentHistoryMapper.toPaymentHistory(paymentEvent)).thenReturn(paymentHistory);
        when(paymentHistoryRepository.save(paymentHistory)).thenReturn(paymentHistory);
        PaymentHistory actual = paymentHistoryService.save(paymentEvent);
        verify(redisPaymentHistoryRepository).save(actual);
        assertThat(actual).isEqualTo(paymentHistory);
    }

    @Test
    public void whenExistsByIdempotencyKeyThenTrue() {
        when(redisPaymentHistoryRepository.existsById(IDEMPOTENCY_KEY.toString())).thenReturn(true);
        boolean result = paymentHistoryService.existsByIdempotencyKey(IDEMPOTENCY_KEY.toString());
        verify(paymentHistoryRepository, times(0)).existsByIdempotencyKey(any());
        assertThat(result).isTrue();
    }

    @Test
    public void whenExistsByIdempotencyKeyThenFalse() {
        when(redisPaymentHistoryRepository.existsById(IDEMPOTENCY_KEY.toString())).thenReturn(false);
        when(paymentHistoryRepository.existsByIdempotencyKey(IDEMPOTENCY_KEY.toString())).thenReturn(false);
        assertThat(paymentHistoryService.existsByIdempotencyKey(IDEMPOTENCY_KEY.toString())).isFalse();
    }
}