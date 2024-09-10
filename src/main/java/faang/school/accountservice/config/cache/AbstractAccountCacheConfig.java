package faang.school.accountservice.config.cache;

import faang.school.accountservice.enums.AccountType;
import lombok.Getter;

@Getter
public abstract class AbstractAccountCacheConfig {
    protected String sequence;
    protected AccountType type;
    protected int numberOfDigits;
    protected int minCacheSize;
    protected int defaultCacheSize;
}