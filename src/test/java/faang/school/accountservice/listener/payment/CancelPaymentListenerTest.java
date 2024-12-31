package faang.school.accountservice.listener.payment;

import faang.school.accountservice.dto.payment.request.CancelPaymentRequest;
import faang.school.accountservice.service.payment.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CancelPaymentListenerTest {

    private static final String TOPIC_NAME = "topic-name";

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private CancelPaymentListener listener;

    private CancelPaymentRequest cancelPaymentRequest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(listener, "topicName", TOPIC_NAME);

        cancelPaymentRequest = CancelPaymentRequest.builder().build();
    }

    @Test
    void testSaveEvent_successful() {
        listener.handleMessage(cancelPaymentRequest);
        verify(paymentService).cancelPayment(cancelPaymentRequest);
    }

    @Test
    void testGetTopic_successful() {
        assertThat(listener.getTopic().getTopic()).isEqualTo(TOPIC_NAME);
    }
}

