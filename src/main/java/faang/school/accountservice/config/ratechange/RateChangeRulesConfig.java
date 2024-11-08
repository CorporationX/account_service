package faang.school.accountservice.config.ratechange;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "rate-change-rules")
@Data
public class RateChangeRulesConfig {
    private Map<String, RateChangeProperties> events;

    public Double getTargetRateChange(String title) {
        return events.containsKey(title) ? events.get(title).getTargetRateChange() : 0.0;
    }

    public String getPartialText(String title) {
        return events.containsKey(title) ? events.get(title).getPartialText() : null;
    }

    @Data
    public static class RateChangeProperties {
        private double targetRateChange;
        private String partialText;
    }
}
