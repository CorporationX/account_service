package faang.school.accountservice.cache;

import faang.school.accountservice.config.cache.AbstractAccountCacheConfig;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public abstract class AbstractFreeAccountNumbersCache<T> implements FreeAccountNumbersCache<T> {

    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    protected ConcurrentHashMap<BigInteger, String> cache;
    protected String sequence;
    protected AccountType type;
    protected int numberOfDigits;
    protected int minCacheSize;
    protected int defaultCacheSize;
    protected AtomicBoolean isGenerating = new AtomicBoolean(false);

    @PostConstruct
    protected void createCache() {
        init();
        generateNumbersToCache(defaultCacheSize);
    }

    public synchronized BigInteger getNumberAndRemoveFromCache() {
        Set<BigInteger> numbers = ConcurrentHashMap.newKeySet();
        numbers.addAll(cache.keySet());
        BigInteger number = numbers.iterator().next();
        cache.remove(number);
        if (isCacheSizeBelowThreshold() && !isGenerating.get()) {
            generateAdditional();
        }
        return number;
    }

    @Async
    protected void generateAdditional() {
        isGenerating.set(true);
        generateNumbersToCache(defaultCacheSize - minCacheSize);
        isGenerating.set(false);
    }

    protected void setConfigProperties(AbstractAccountCacheConfig config) {
        cache = new ConcurrentHashMap<>();
        sequence = config.getSequence();
        type = config.getType();
        numberOfDigits = config.getNumberOfDigits();
        minCacheSize = config.getMinCacheSize();
        defaultCacheSize = config.getDefaultCacheSize();
    }

    private void generateNumbersToCache(int quantity) {
        Set<BigInteger> cacheKeySet = cache.keySet(type.toString());
        List<Long> freeNumbersList = freeAccountNumbersRepository.getFreeAccountNumbers(quantity, sequence);
        for (Long number : freeNumbersList) {
            cacheKeySet.add(createFreeNumber(number, numberOfDigits));
        }
    }

    private BigInteger createFreeNumber(long number, int numberOfDigits) {
        int prefix = AccountType.valueOf(type.toString()).getPrefix();
        int numberOfDigitsInPrefix = String.valueOf(prefix).length();
        BigInteger prefixNumber = BigInteger.valueOf(prefix)
                .multiply(BigInteger.TEN.pow(numberOfDigits - numberOfDigitsInPrefix));
        return prefixNumber.add(BigInteger.valueOf(number));
    }

    private boolean isCacheSizeBelowThreshold() {
        return cache.keySet().size() < minCacheSize;
    }

    protected abstract void init();
}