package faang.school.accountservice.service;

import faang.school.accountservice.cache.FreeAccountNumbersCache;
import faang.school.accountservice.enums.AccountType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {

    private final List<FreeAccountNumbersCache<AccountType>> freeAccountNumbersCacheList;

    public BigInteger getFreeNumberAndDeleteFromCache(AccountType accountType) {
        Optional<FreeAccountNumbersCache<AccountType>> cache = freeAccountNumbersCacheList.stream()
                .filter(c -> c.getAccountType().equals(accountType))
                .findFirst();
        return cache.orElseThrow(() -> {
            log.info("Cache is not exist for this account type: {}", accountType);
            return new RuntimeException("Cache is not exist for this account type " + accountType);
        }).getNumberAndRemoveFromCache();
    }
}