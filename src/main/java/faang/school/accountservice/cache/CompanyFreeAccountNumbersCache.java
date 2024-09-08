package faang.school.accountservice.cache;

import faang.school.accountservice.config.cache.CompanyAccountCacheConfig;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import org.springframework.stereotype.Component;

@Component
public class CompanyFreeAccountNumbersCache extends AbstractFreeAccountNumbersCache<AccountType> {

    private final CompanyAccountCacheConfig companyAccountCacheConfig;

    public CompanyFreeAccountNumbersCache(FreeAccountNumbersRepository freeAccountNumbersRepository,
                                          CompanyAccountCacheConfig companyAccountCacheConfig) {
        super(freeAccountNumbersRepository);
        this.companyAccountCacheConfig = companyAccountCacheConfig;
    }

    @Override
    public AccountType getAccountType() {
        return companyAccountCacheConfig.getType();
    }

    @Override
    protected void init() {
        super.setConfigProperties(companyAccountCacheConfig);
    }
}