package faang.school.accountservice.service.account.number;

import faang.school.accountservice.config.properties.AccountNumberProperties;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.AccountNumberGenerationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountNumberGeneratorImpl implements AccountNumberGenerator {

    private static final int MIN_SEQUENCE_VALUE = 0;
    private static final int DECIMAL_BASE = 10;
    private static final long MULTIPLICATIVE_IDENTITY = 1L;

    private final AccountNumberProperties accountNumberProperties;

    @Override
    public long generate(AccountType type, long sequence) {
        AccountNumberProperties.Format format = accountNumberProperties.getFormats().get(type);
        if (format == null) {
            throw new AccountNumberGenerationException("No format for type " + type);
        }

        if (sequence < MIN_SEQUENCE_VALUE) {
            throw new AccountNumberGenerationException("Sequence must be non-negative: " + sequence);
        }

        int digits = format.getCounterDigits();
        long prefix = format.getPrefix();

        long pow10 = pow10Safe(digits);
        if (sequence >= pow10) {
            throw new AccountNumberGenerationException("Sequence exceeds digit limit: " + sequence);
        }

        long base = Math.multiplyExact(prefix, pow10);
        return Math.addExact(base, sequence);
    }

    private long pow10Safe(int exponent) {
        long result = MULTIPLICATIVE_IDENTITY;
        for (int i = 0; i < exponent; i++) {
            result = Math.multiplyExact(result, DECIMAL_BASE);
        }
        return result;
    }
}
