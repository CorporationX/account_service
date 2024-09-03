package faang.school.accountservice.cache;

import java.math.BigInteger;

public interface FreeAccountNumbersCache<T> {
    BigInteger getNumberAndRemoveFromCache();
    T getAccountType();
}
