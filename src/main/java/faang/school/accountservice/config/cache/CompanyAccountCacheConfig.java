package faang.school.accountservice.config.cache;

import faang.school.accountservice.enums.AccountType;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class CompanyAccountCacheConfig extends AbstractAccountCacheConfig {

    @Value("${spring.account-cache.company.sequence}")
    private String sequence;

    private final AccountType type = AccountType.COMPANY;

    @Value("${spring.account-cache.company.number-of-digits}")
    private int numberOfDigits;

    @Value("${spring.account-cache.company.min-cache-size}")
    private int minCacheSize;

    @Value("${spring.account-cache.company.default-cache-size}")
    private int defaultCacheSize;
}