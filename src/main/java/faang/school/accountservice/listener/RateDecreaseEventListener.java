package faang.school.accountservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.config.ratechange.RateChangeRulesConfig;
import faang.school.accountservice.feign.AchievementServiceClient;
import faang.school.accountservice.model.dto.AchievementDto;
import faang.school.accountservice.model.event.AchievementEvent;
import faang.school.accountservice.model.event.RateDecreaseEvent;
import faang.school.accountservice.service.RateAdjustmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RateDecreaseEventListener extends AbstractEventListener<RateDecreaseEvent> implements MessageListener {

    private final RateAdjustmentService rateAdjustmentService;
    private final RateChangeRulesConfig rateChangeRulesConfig;
    private final AchievementServiceClient achievementServiceClient;

    @Autowired
    public RateDecreaseEventListener(ObjectMapper objectMapper,
                                    RateAdjustmentService rateAdjustmentService,
                                    RateChangeRulesConfig rateChangeRulesConfig,
                                    AchievementServiceClient achievementServiceClient) {
        super(objectMapper);
        this.rateAdjustmentService = rateAdjustmentService;
        this.rateChangeRulesConfig = rateChangeRulesConfig;
        this.achievementServiceClient = achievementServiceClient;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        handleEvent(message, RateDecreaseEvent.class, event -> {
            Double rateChange = rateChangeRulesConfig.getRateChange(event.getTitle());
            if (rateChange != 0.0) {
                event.getUserIds().forEach(userId -> rateAdjustmentService.adjustRate(userId, rateChange));
            }
        });
    }
}
