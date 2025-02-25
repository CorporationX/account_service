package faang.school.accountservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Data
@Configuration
@ConfigurationProperties(prefix = "account")
public class AccountProperties {

    private int validityPeriod;

    private BigDecimal startBonus;

    private Number number;

    @Data
    public static class Number {

        private int minDigits;

        private int maxDigits;
    }
}