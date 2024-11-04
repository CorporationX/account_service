package faang.school.accountservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.PendingDto;
import faang.school.accountservice.service.BalanceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentAuthorizationListenerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private BalanceService balanceService;

    @Mock
    private Message message;

    @InjectMocks
    private PaymentAuthorizationEventListener paymentAuthorizationListener;

    @Test
    void testOnMessage_Success() throws IOException {
        PendingDto pendingDto = new PendingDto();
        pendingDto.setAmount(BigDecimal.valueOf(100.0));

        byte[] messageBody = new byte[0];
        when(message.getBody()).thenReturn(messageBody);
        when(objectMapper.readValue(messageBody, PendingDto.class)).thenReturn(pendingDto);

        paymentAuthorizationListener.onMessage(message, null);

        verify(balanceService, times(1)).paymentAuthorization(pendingDto);
    }

    @Test
    void testOnMessage_IOException() throws IOException {
        byte[] messageBody = new byte[]{/* invalid bytes */};
        when(message.getBody()).thenReturn(messageBody);
        when(objectMapper.readValue(messageBody, PendingDto.class)).thenThrow(new IOException("Deserialization failed"));

        assertThrows(RuntimeException.class, () -> paymentAuthorizationListener.onMessage(message, null));
        verify(balanceService, never()).paymentAuthorization(any());
    }
}
