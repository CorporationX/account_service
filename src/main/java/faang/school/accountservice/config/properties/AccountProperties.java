package faang.school.accountservice.config.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "account")
@Getter
@Setter
@Validated
public class AccountProperties {
    @Min(1)
    @Max(1000)
    private int maxActiveAccountsPerOwner = 10;
}