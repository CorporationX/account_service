package faang.school.accountservice.listener;

import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgressPaymentListenerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private BalanceService balanceService;

    @Mock
    private Message message;

    @InjectMocks
    private ProgressPaymentListener progressPaymentListener;

    @Test
    void testOnMessage_Success() throws IOException {
        PendingDto pendingDto1 = new PendingDto();
        pendingDto1.setAmount(BigDecimal.valueOf(100.0));
        PendingDto pendingDto2 = new PendingDto();
        pendingDto2.setAmount(BigDecimal.valueOf(50.0));
        List<PendingDto> pendingDtoList = Arrays.asList(pendingDto1, pendingDto2);

        byte[] messageBody = new byte[0];
        when(message.getBody()).thenReturn(messageBody);
        when(objectMapper.readValue(eq(messageBody), any(TypeReference.class))).thenReturn(pendingDtoList);

        progressPaymentListener.onMessage(message, null);

        verify(balanceService, times(1)).submitPayment(pendingDtoList);
    }

    @Test
    void testOnMessage_IOException() throws IOException {
        byte[] messageBody = new byte[1];
        when(message.getBody()).thenReturn(messageBody);
        when(objectMapper.readValue(messageBody, new TypeReference<List<PendingDto>>() {
        }))
                .thenThrow(new IOException("Deserialization failed"));

        assertThrows(RuntimeException.class, () -> progressPaymentListener.onMessage(message, null));
        verify(balanceService, never()).submitPayment(any());
    }
}
