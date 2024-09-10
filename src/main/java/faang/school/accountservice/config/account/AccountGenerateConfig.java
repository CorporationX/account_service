package faang.school.accountservice.config.account;

import faang.school.accountservice.enums.AccountType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "task.accounts-generation.types")
public class AccountGenerateConfig {
    private ConcurrentHashMap<AccountType, Integer> accountsQuantity =new ConcurrentHashMap<>();
}
