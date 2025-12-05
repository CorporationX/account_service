package faang.school.accountservice.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "account")
@Getter
@Setter
public class AccountProperties {
    private int maxActiveAccountsPerOwner = 10;
}