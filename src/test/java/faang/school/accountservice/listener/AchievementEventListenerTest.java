package faang.school.accountservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.config.ratechange.RateChangeRulesConfig;
import faang.school.accountservice.exception.EventProcessingException;
import faang.school.accountservice.feign.AchievementServiceClient;
import faang.school.accountservice.model.dto.AchievementDto;
import faang.school.accountservice.model.event.AchievementEvent;
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

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AchievementEventListenerTest {

    @Mock
    private RateAdjustmentService rateAdjustmentService;

    @Mock
    private RateChangeRulesConfig rateChangeRulesConfig;

    @Mock
    private AchievementServiceClient achievementServiceClient;

    @Mock
    private Message message;

    @InjectMocks
    private AchievementEventListener achievementEventListener;

    private ObjectMapper objectMapper;
    private AchievementEvent event;
    private AchievementDto achievementDto;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();

        event = new AchievementEvent();
        event.setUserId(1L);
        event.setAchievementId(123L);

        achievementDto = new AchievementDto();
        achievementDto.setTitle("writer");

        achievementEventListener = new AchievementEventListener(
                objectMapper,
                rateAdjustmentService,
                rateChangeRulesConfig,
                achievementServiceClient
        );
    }

    @Test
    @DisplayName("Should adjust rate when rate change is not zero")
    public void testOnMessage_Success() throws IOException {
        String eventJson = objectMapper.writeValueAsString(event);

        when(message.getBody()).thenReturn(eventJson.getBytes(StandardCharsets.UTF_8));
        when(achievementServiceClient.getAchievement(event.getAchievementId())).thenReturn(achievementDto);
        when(rateChangeRulesConfig.getRateChange(achievementDto.getTitle())).thenReturn(0.1);

        achievementEventListener.onMessage(message, null);

        verify(rateAdjustmentService).adjustRate(event.getUserId(), 0.1);
    }

    @Test
    @DisplayName("Should not adjust rate when rate change is zero")
    public void testOnMessage_SkipOperation() throws IOException {
        String eventJson = objectMapper.writeValueAsString(event);

        when(message.getBody()).thenReturn(eventJson.getBytes(StandardCharsets.UTF_8));
        when(achievementServiceClient.getAchievement(event.getAchievementId())).thenReturn(achievementDto);
        when(rateChangeRulesConfig.getRateChange(achievementDto.getTitle())).thenReturn(0.0);

        achievementEventListener.onMessage(message, null);

        verify(rateAdjustmentService, never()).adjustRate(anyLong(), anyDouble());
    }

    @Test
    @DisplayName("Should handle IOException gracefully")
    public void testOnMessage_IOException() {
        when(message.getBody()).thenReturn("invalid json".getBytes(StandardCharsets.UTF_8));

        try {
            achievementEventListener.onMessage(message, null);
        } catch (EventProcessingException e) {
            verify(rateAdjustmentService, never()).adjustRate(anyLong(), anyDouble());
        }
    }
}
