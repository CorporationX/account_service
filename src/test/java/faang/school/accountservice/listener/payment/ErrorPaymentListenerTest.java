package faang.school.accountservice.listener.payment;

import faang.school.accountservice.dto.payment.request.ErrorPaymentRequest;
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
class ErrorPaymentListenerTest {

    private static final String TOPIC_NAME = "topic-name";

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private ErrorPaymentListener listener;

    private ErrorPaymentRequest errorPaymentRequest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(listener, "topicName", TOPIC_NAME);
        errorPaymentRequest = ErrorPaymentRequest.builder().build();
    }

    @Test
    void testSaveEvent_successful() {
        listener.handleMessage(errorPaymentRequest);
        verify(paymentService).errorPayment(errorPaymentRequest);
    }

    @Test
    void testGetTopic_successful() {
        assertThat(listener.getTopic().getTopic()).isEqualTo(TOPIC_NAME);
    }
}

