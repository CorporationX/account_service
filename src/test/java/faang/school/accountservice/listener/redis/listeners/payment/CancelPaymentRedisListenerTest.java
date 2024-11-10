package faang.school.accountservice.listener.redis.listeners.payment;

import faang.school.accountservice.dto.payment.request.CancelPaymentRequest;
import faang.school.accountservice.service.balance.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static faang.school.accountservice.util.fabrics.PaymentsFabric.buildCancelPaymentRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CancelPaymentRedisListenerTest {
    private static final String TOPIC_NAME = "topic-name";

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private CancelPaymentRedisListener cancelPaymentRedisListener;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cancelPaymentRedisListener, "topicName", TOPIC_NAME);
    }

    @Test
    void testSaveEvent_successful() {
        CancelPaymentRequest cancelPaymentRequest = buildCancelPaymentRequest();
        cancelPaymentRedisListener.saveEvent(cancelPaymentRequest);

        verify(balanceService).cancelPayment(cancelPaymentRequest);
    }

    @Test
    void testGetTopic_successful() {
        assertThat(cancelPaymentRedisListener.getTopic().getTopic()).isEqualTo(TOPIC_NAME);
    }

    @Test
    void testHandleException_successful() {
        assertThatThrownBy(() -> cancelPaymentRedisListener.handleException(new RuntimeException()))
                .isInstanceOf(RuntimeException.class);
    }
}
