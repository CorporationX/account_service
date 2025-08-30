package faang.school.accountservice.utils;

import faang.school.accountservice.exception.AccountNumberGenerationException;

public final class DecimalPowers {

    private static final int DECIMAL_BASE = 10;
    private static final int MAX_SUPPORTED_EXPONENT = 18;
    private static final int MIN_EXPONENT = 0;
    private static final int POWERS_ARRAY_SIZE = MAX_SUPPORTED_EXPONENT + 1;
    private static final long[] POWERS_OF_TEN = new long[POWERS_ARRAY_SIZE];

    static {
        POWERS_OF_TEN[0] = 1L;
        for (int i = 1; i <= MAX_SUPPORTED_EXPONENT; i++) {
            POWERS_OF_TEN[i] = Math.multiplyExact(POWERS_OF_TEN[i - 1], DECIMAL_BASE);
        }
    }

    private DecimalPowers() {
    }

    public static long pow10(int exponent) {
        if (exponent < MIN_EXPONENT || exponent > MAX_SUPPORTED_EXPONENT) {
            throw new AccountNumberGenerationException("Invalid exponent: " + exponent);
        }
        return POWERS_OF_TEN[exponent];
    }

    public static int maxSupportedExponent() {
        return MAX_SUPPORTED_EXPONENT;
    }
}
