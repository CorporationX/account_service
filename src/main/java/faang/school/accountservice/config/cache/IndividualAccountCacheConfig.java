package faang.school.accountservice.config.cache;

import faang.school.accountservice.enums.AccountType;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class IndividualAccountCacheConfig {

    @Value("${spring.account-cache.individual.sequence}")
    private String sequence;

    private final AccountType type = AccountType.INDIVIDUAL;

    @Value("${spring.account-cache.individual.number-of-digits}")
    private int numberOfDigits;

    @Value("${spring.account-cache.individual.min-cache-size}")
    private int minCacheSize;

    @Value("${spring.account-cache.individual.default-cache-size}")
    private int defaultCacheSize;
}