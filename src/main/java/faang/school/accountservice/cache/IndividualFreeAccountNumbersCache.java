package faang.school.accountservice.cache;

import faang.school.accountservice.config.cache.IndividualAccountCacheConfig;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import org.springframework.stereotype.Component;

@Component
public class IndividualFreeAccountNumbersCache extends AbstractFreeAccountNumbersCache<AccountType> {

    private final IndividualAccountCacheConfig individualAccountCacheConfig;

    public IndividualFreeAccountNumbersCache(FreeAccountNumbersRepository freeAccountNumbersRepository,
                                             IndividualAccountCacheConfig individualAccountCacheConfig) {
        super(freeAccountNumbersRepository);
        this.individualAccountCacheConfig = individualAccountCacheConfig;
    }

    @Override
    public AccountType getAccountType() {
        return individualAccountCacheConfig.getType();
    }

    @Override
    protected void init() {
        super.setConfigProperties(individualAccountCacheConfig);
    }
}