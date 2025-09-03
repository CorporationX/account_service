package faang.school.accountservice.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import java.math.RoundingMode;

@ConfigurationProperties(prefix = "balance")
public record BalanceProps(
        @DefaultValue("2") int scale,
        @DefaultValue("5") int divideScale,
        @DefaultValue("HALF_UP") RoundingMode roundingMode
) {}
