package faang.school.accountservice.config.account;

import faang.school.accountservice.enums.AccountType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "task.accounts-generation.types")
public class AccountGenerateConfig {
    private Map<AccountType, Integer> accountsQuantity =new HashMap<>();
}
