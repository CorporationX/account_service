package faang.school.accountservice.config.ratechange;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "rate-change-rules")
@Data
@Slf4j
public class RateChangeRulesConfig {
    private Map<String, Double> events;

    public Double getRateChange(String title) {
        return events.getOrDefault(title, 0.0);
    }
}
