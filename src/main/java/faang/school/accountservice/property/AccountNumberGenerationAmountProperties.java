package faang.school.accountservice.property;

import faang.school.accountservice.model.enums.AccountType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "scheduling.account-number-generation")
public class AccountNumberGenerationAmountProperties {
    private Map<AccountType, Integer> amounts;
}
