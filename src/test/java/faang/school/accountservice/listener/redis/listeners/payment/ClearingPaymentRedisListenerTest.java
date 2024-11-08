package faang.school.accountservice.listener.redis.listeners.payment;

import faang.school.accountservice.dto.payment.request.ClearingPaymentRequest;
import faang.school.accountservice.service.balance.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static faang.school.accountservice.util.fabrics.PaymentsFabric.buildClearingPaymentRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClearingPaymentRedisListenerTest {
    private static final String TOPIC_NAME = "topic-name";

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private ClearingPaymentRedisListener clearingPaymentRedisListener;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(clearingPaymentRedisListener, "topicName", TOPIC_NAME);
    }

    @Test
    void testSaveEvent_successful() {
        ClearingPaymentRequest clearingPaymentRequest = buildClearingPaymentRequest();
        clearingPaymentRedisListener.saveEvent(clearingPaymentRequest);

        verify(balanceService).clearingPayment(clearingPaymentRequest);
    }

    @Test
    void testGetTopic_successful() {
        assertThat(clearingPaymentRedisListener.getTopic().getTopic()).isEqualTo(TOPIC_NAME);
    }

    @Test
    void testHandleException_successful() {
        assertThatThrownBy(() -> clearingPaymentRedisListener.handleException(new RuntimeException()))
                .isInstanceOf(RuntimeException.class);
    }
}
