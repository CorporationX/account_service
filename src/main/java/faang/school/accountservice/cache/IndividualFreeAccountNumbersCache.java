package faang.school.accountservice.cache;

import faang.school.accountservice.config.cache.IndividualAccountCacheConfig;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class IndividualFreeAccountNumbersCache extends AbstractFreeAccountNumbersCache<AccountType> {

    private final IndividualAccountCacheConfig individualAccountCacheConfig;

    public IndividualFreeAccountNumbersCache(FreeAccountNumbersRepository freeAccountNumbersRepository,
                                             IndividualAccountCacheConfig individualAccountCacheConfig) {
        super(freeAccountNumbersRepository);
        this.individualAccountCacheConfig = individualAccountCacheConfig;
    }

    @Override
    protected void init() {
        super.cache = new ConcurrentHashMap<>();
        super.sequence = individualAccountCacheConfig.getSequence();
        super.type = individualAccountCacheConfig.getType();
        super.numberOfDigits = individualAccountCacheConfig.getNumberOfDigits();
        super.minCacheSize = individualAccountCacheConfig.getMinCacheSize();
        super.defaultCacheSize = individualAccountCacheConfig.getDefaultCacheSize();
    }

    @Override
    public AccountType getAccountType() {
        return individualAccountCacheConfig.getType();
    }
}
