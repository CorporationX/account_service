package faang.school.accountservice.listener.kafka.listeners.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.AuthPaymentRequest;
import faang.school.accountservice.dto.payment.request.ClearingPaymentRequest;
import faang.school.accountservice.service.balance.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static faang.school.accountservice.util.fabrics.PaymentsFabric.buildClearingPaymentRequest;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClearingPaymentKafkaListenerTest {
    private static final String TOPIC_NAME = "topic-name";
    private static final String MESSAGE = "{\"message\":\"This is a valid message\"}";

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private ClearingPaymentKafkaListener clearingPaymentKafkaListener;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(clearingPaymentKafkaListener, "topicName", TOPIC_NAME);
    }

    @Test
    void testOnMessage_exception() throws IOException {
        when(objectMapper.readValue(MESSAGE, AuthPaymentRequest.class)).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> clearingPaymentKafkaListener.onMessage(MESSAGE))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void testOnMessage_successful() throws IOException {
        ClearingPaymentRequest clearingPaymentRequest = buildClearingPaymentRequest();
        when(objectMapper.readValue(MESSAGE, ClearingPaymentRequest.class)).thenReturn(clearingPaymentRequest);

        clearingPaymentKafkaListener.onMessage(MESSAGE);

        verify(balanceService).clearingPayment(clearingPaymentRequest);
    }
}
