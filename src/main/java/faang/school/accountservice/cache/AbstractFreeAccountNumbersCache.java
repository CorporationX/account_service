package faang.school.accountservice.cache;

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
    protected T type;
    protected int numberOfDigits;
    protected int minCacheSize;
    protected int defaultCacheSize;
    protected AtomicBoolean isGenerating = new AtomicBoolean(false);

    @PostConstruct
    protected void createCache() {
        init();
        generateNumbersToCache(defaultCacheSize);
    }

    public BigInteger getNumberAndRemoveFromCache() {
        Set<BigInteger> numbers = cache.keySet();
        BigInteger number = numbers.iterator().next();
        cache.remove(number);
        if (lessThanMin() && !isGenerating.get()) {
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
        BigInteger prefixNumber = BigInteger
                .valueOf(prefix * (long) Math.pow(10, numberOfDigits - numberOfDigitsInPrefix));
        return prefixNumber.add(BigInteger.valueOf(number));
    }

    private boolean lessThanMin() {
        return cache.keySet().size() < minCacheSize;
    }

    protected abstract void init();
}