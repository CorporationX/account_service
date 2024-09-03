package faang.school.accountservice.cache;

import faang.school.accountservice.config.cache.CompanyAccountCacheConfig;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class CompanyFreeAccountNumbersCache extends AbstractFreeAccountNumbersCache<AccountType> {

    private final CompanyAccountCacheConfig companyAccountCacheConfig;

    public CompanyFreeAccountNumbersCache(FreeAccountNumbersRepository freeAccountNumbersRepository,
                                          CompanyAccountCacheConfig companyAccountCacheConfig) {
        super(freeAccountNumbersRepository);
        this.companyAccountCacheConfig = companyAccountCacheConfig;
    }

    @Override
    protected void init() {
        super.cache = new ConcurrentHashMap<>();
        super.sequence = companyAccountCacheConfig.getSequence();
        super.type = companyAccountCacheConfig.getType();
        super.numberOfDigits = companyAccountCacheConfig.getNumberOfDigits();
        super.minCacheSize = companyAccountCacheConfig.getMinCacheSize();
        super.defaultCacheSize = companyAccountCacheConfig.getDefaultCacheSize();
    }

    @Override
    public AccountType getAccountType() {
        return companyAccountCacheConfig.getType();
    }
}
