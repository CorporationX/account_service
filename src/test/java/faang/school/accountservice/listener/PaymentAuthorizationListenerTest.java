package faang.school.accountservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.Currency;
import faang.school.accountservice.dto.PendingDto;
import faang.school.accountservice.service.BalanceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentAuthorizationListenerTest {

    @Mock
    private BalanceService balanceService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private PaymentAuthorizationListener listener;

    @Test
    public void testOnEvent_Success() throws JsonProcessingException {
        String jsonEvent = "{\"amount\":100.00,\"currency\":\"USD\",\"fromAccountId\":1,\"toAccountId\":2}";
        PendingDto pendingDto = PendingDto.builder()
                .amount(new BigDecimal("100.00"))
                .currency(Currency.USD)
                .fromAccountId(1L)
                .toAccountId(2L)
                .build();
        when(objectMapper.readValue(jsonEvent, PendingDto.class)).thenReturn(pendingDto);

        listener.onEvent(jsonEvent, acknowledgment);

        verify(balanceService).paymentAuthorization(pendingDto);
        verify(acknowledgment).acknowledge();
    }

    @Test
    public void testOnEvent_JsonProcessingException() throws JsonProcessingException {
        String jsonEvent = "invalid_json";
        when(objectMapper.readValue(jsonEvent, PendingDto.class)).thenThrow(JsonProcessingException.class);

        listener.onEvent(jsonEvent, acknowledgment);

        verify(balanceService, never()).paymentAuthorization(any(PendingDto.class));
        verify(acknowledgment, never()).acknowledge();
    }
}