package faang.school.accountservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.config.ratechange.RateChangeRulesConfig;
import faang.school.accountservice.exception.EventProcessingException;
import faang.school.accountservice.feign.AchievementServiceClient;
import faang.school.accountservice.model.event.RateDecreaseEvent;
import faang.school.accountservice.service.RateAdjustmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateDecreaseEventListenerTest {

    @Mock
    private RateAdjustmentService rateAdjustmentService;

    @Mock
    private RateChangeRulesConfig rateChangeRulesConfig;

    @Mock
    private AchievementServiceClient achievementServiceClient;

    @Mock
    private Message message;

    @InjectMocks
    private RateDecreaseEventListener rateDecreaseEventListener;

    private ObjectMapper objectMapper;
    private RateDecreaseEvent event;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();

        event = new RateDecreaseEvent();
        event.setUserIds(Arrays.asList(1L, 2L));
        event.setTitle("expletives");

        rateDecreaseEventListener = new RateDecreaseEventListener(
                objectMapper,
                rateAdjustmentService,
                rateChangeRulesConfig,
                achievementServiceClient
        );
    }

    @Test
    @DisplayName("Should adjust rate for each user when rate change is not zero")
    public void testOnMessage_Success() throws IOException {
        String eventJson = objectMapper.writeValueAsString(event);

        when(message.getBody()).thenReturn(eventJson.getBytes(StandardCharsets.UTF_8));
        when(rateChangeRulesConfig.getRateChange(event.getTitle())).thenReturn(0.2);

        rateDecreaseEventListener.onMessage(message, null);

        verify(rateAdjustmentService, times(2)).adjustRate(anyLong(), eq(0.2));
    }

    @Test
    @DisplayName("Should not adjust rate when rate change is zero")
    public void testOnMessage_SkipOperation() throws IOException {
        String eventJson = objectMapper.writeValueAsString(event);

        when(message.getBody()).thenReturn(eventJson.getBytes(StandardCharsets.UTF_8));
        when(rateChangeRulesConfig.getRateChange(event.getTitle())).thenReturn(0.0);

        rateDecreaseEventListener.onMessage(message, null);

        verify(rateAdjustmentService, never()).adjustRate(anyLong(), anyDouble());
    }

    @Test
    @DisplayName("Should handle IOException gracefully")
    public void testOnMessage_IOException() {
        when(message.getBody()).thenReturn("invalid json".getBytes(StandardCharsets.UTF_8));

        try {
            rateDecreaseEventListener.onMessage(message, null);
        } catch (EventProcessingException e) {
            verify(rateAdjustmentService, never()).adjustRate(anyLong(), anyDouble());
        }
    }
}
