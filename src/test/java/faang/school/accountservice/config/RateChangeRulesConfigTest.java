package faang.school.accountservice.config;

import faang.school.accountservice.config.ratechange.RateChangeRulesConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")
public class RateChangeRulesConfigTest {

    @Autowired
    private RateChangeRulesConfig rateChangeRulesConfig;

    @Test
    @DisplayName("Should load 'writer' rate correctly from configuration")
    public void getRateChange_Success() {
        Double rate = rateChangeRulesConfig.getTargetRateChange("WRITER");
        assertEquals(100500, rate, "The 'writer' rate should be 100500");
    }

    @Test
    @DisplayName("Should return 0.0 for an unknown title")
    public void getRateChange_UnknownTitle() {
        Double rate = rateChangeRulesConfig.getTargetRateChange("unknown");
        assertEquals(0.0, rate, "The rate for an unknown title should be 0.0");
    }

    @Test
    @DisplayName("Should load partial text correctly from configuration")
    public void getPartialText_Success() {
        String text = rateChangeRulesConfig.getPartialText("WRITER");
        assertEquals("for getting WRITER achievement", text,
                "The partial text should be 'for getting WRITER achievement'");
    }

    @Test
    @DisplayName("Should return null for an unknown title")
    public void getPartialText_UnknownTitle() {
        String text = rateChangeRulesConfig.getPartialText("unknown");
        assertNull(text, "The partial text should be null");
    }
}
