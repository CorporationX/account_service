package faang.school.accountservice.publisher;

import faang.school.accountservice.config.redis.RedisProperties;
import faang.school.accountservice.dto.PendingDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentStatusChangePublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RedisProperties redisProperties;

    @InjectMocks
    private PaymentStatusChangePublisher paymentStatusChangePublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPublishEvent() {
        PendingDto pendingDto = new PendingDto();
        pendingDto.setAmount(BigDecimal.valueOf(100.0));
        when(redisProperties.getChannels()).thenReturn(Map.of("result_progress_payment", "test_channel"));

        paymentStatusChangePublisher.publish(pendingDto);

        verify(redisTemplate, times(1)).convertAndSend("test_channel", pendingDto);
    }
}
