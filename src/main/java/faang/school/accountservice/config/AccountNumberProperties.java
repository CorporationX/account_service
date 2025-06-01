package faang.school.accountservice.config;

import faang.school.accountservice.enums.AccountNumberType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "account.number")
public class AccountNumberProperties {

    private Map<AccountNumberType, Integer> prefixes = new HashMap<>();
}
