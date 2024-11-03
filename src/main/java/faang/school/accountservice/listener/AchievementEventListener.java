package faang.school.accountservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.model.event.AchievementEvent;
import faang.school.accountservice.service.RateAdjustmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AchievementEventListener implements MessageListener {

    private final ObjectMapper objectMapper;
    private final RateAdjustmentService rateAdjustmentService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String messageBody = new String(message.getBody());
            AchievementEvent event = objectMapper.readValue(messageBody, AchievementEvent.class);

            log.info("Received AchievementEvent: {}", event);
            rateAdjustmentService.adjustRateForAchievement(event.getUserId(), event.getTitle());

        } catch (Exception e) {
            log.error("Failed to process AchievementEvent", e);
        }
    }
}
