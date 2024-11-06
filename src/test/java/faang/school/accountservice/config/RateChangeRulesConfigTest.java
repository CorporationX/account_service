package faang.school.accountservice.config;

import faang.school.accountservice.config.ratechange.RateChangeRulesConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class RateChangeRulesConfigTest {

    @Autowired
    private RateChangeRulesConfig rateChangeRulesConfig;

    @Test
    @DisplayName("Should load 'writer' rate correctly from configuration")
    public void getRateChange_Success() {
        Double rate = rateChangeRulesConfig.getRateChange("writer");
        assertEquals(100500, rate, "The 'writer' rate should be 100500");
    }

    @Test
    @DisplayName("Should return 0.0 for an unknown title")
    public void getRateChange_UnknownTitle() {
        Double rate = rateChangeRulesConfig.getRateChange("unknown");
        assertEquals(0.0, rate, "The rate for an unknown title should be 0.0");
    }
}
