package faang.school.accountservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PercentageCalculatorTest {

    private PercentageCalculator percentageCalculator;

    @BeforeEach
    void setUp() {
        percentageCalculator = new PercentageCalculator();
    }

    @Test
    void calculatePercentageNumber_ShouldReturnCorrectValue_WhenValidInput() {
        BigDecimal value = new BigDecimal("200");
        BigDecimal percent = new BigDecimal("10");

        BigDecimal result = percentageCalculator.calculatePercentageNumber(value, percent);

        assertNotNull(result);
        assertEquals(new BigDecimal("20"), result);
    }
}
