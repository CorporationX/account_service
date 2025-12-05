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
    private Map<AccountType, String> prefix;
    private int bodyLength;
    private int batchSize;
    private int maxRetries;
}