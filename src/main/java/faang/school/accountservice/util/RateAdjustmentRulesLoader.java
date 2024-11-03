package faang.school.accountservice.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class RateAdjustmentRulesLoader {
    private final Map<String, Double> achievementIncreaseRates;

    @Autowired
    public RateAdjustmentRulesLoader(ResourceLoader resourceLoader, ObjectMapper objectMapper) throws IOException, IOException {
        Resource resource = resourceLoader.getResource("classpath:rate-adjustment-rules.json");
        Map<String, Object> rules = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {});
        this.achievementIncreaseRates = (Map<String, Double>) rules.get("achievement_increase");
    }

    public Double getIncreaseRate(String achievementTitle) {
        return achievementIncreaseRates.get(achievementTitle);
    }
}
