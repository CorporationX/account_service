package faang.school.accountservice.config.property;

import faang.school.accountservice.enums.AccountType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "account")
public class AccountTypeProperties {
    private Map<AccountType, String> type;

    public String getValue(AccountType type) {
        return this.type.get(type);
    }
}
