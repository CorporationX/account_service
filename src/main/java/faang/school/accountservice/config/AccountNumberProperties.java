package faang.school.accountservice.config;

import faang.school.accountservice.enums.AccountType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "account.number")
public class AccountNumberProperties {
    private Map<AccountType, Long> prefix;
    private int batchSize = 1;
    private int maxRetries = 10;
}
