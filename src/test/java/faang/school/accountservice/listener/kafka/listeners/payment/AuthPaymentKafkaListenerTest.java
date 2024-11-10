package faang.school.accountservice.listener.kafka.listeners.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.AuthPaymentRequest;
import faang.school.accountservice.service.balance.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static faang.school.accountservice.util.fabrics.PaymentsFabric.buildAuthPaymentRequest;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthPaymentKafkaListenerTest {
    private static final String TOPIC_NAME = "topic-name";
    private static final String MESSAGE = "{\"message\":\"This is a valid message\"}";

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private AuthPaymentKafkaListener authPaymentKafkaListener;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authPaymentKafkaListener, "topicName", TOPIC_NAME);
    }

    @Test
    void testOnMessage_exception() throws IOException {
        when(objectMapper.readValue(MESSAGE, AuthPaymentRequest.class)).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> authPaymentKafkaListener.onMessage(MESSAGE))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void testOnMessage_successful() throws IOException {
        AuthPaymentRequest authPaymentRequest = buildAuthPaymentRequest();
        when(objectMapper.readValue(MESSAGE, AuthPaymentRequest.class)).thenReturn(authPaymentRequest);

        authPaymentKafkaListener.onMessage(MESSAGE);

        verify(balanceService).authorizePayment(authPaymentRequest);
    }
}
