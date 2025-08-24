package faang.school.accountservice.config.properties;

import faang.school.accountservice.enums.AccountType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.EnumMap;
import java.util.Map;

@Data
@Validated
@ConfigurationProperties(prefix = "account.number")
public class AccountNumberProperties {

    @NotNull
    private Map<AccountType, Format> formats = new EnumMap<>(AccountType.class);

    @Data
    public static class Format {

        @Min(1000) @Max(9999)
        private long prefix;

        @Min(1) @Max(12)
        private int counterDigits;
    }
}