package faang.school.accountservice.config.context;

import faang.school.accountservice.enums.AccountType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "account.type.number")
@Getter @Setter
public class AccountTypeNumberProperties {

    private Map<AccountType, Integer> firstNumbers;
}
