package faang.school.accountservice.service.account.number;

import faang.school.accountservice.config.properties.AccountNumberProperties;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.AccountNumberGenerationException;
import faang.school.accountservice.utils.DecimalPowers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountNumberGeneratorImpl implements AccountNumberGenerator {

    private final AccountNumberProperties accountNumberProperties;

    private static final int MIN_SEQUENCE_VALUE = 0;

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
        long pow10  = DecimalPowers.pow10(digits);

        if (sequence >= pow10) {
            throw new AccountNumberGenerationException("Sequence exceeds digit limit: " + sequence);
        }

        long base = Math.multiplyExact(prefix, pow10);
        return Math.addExact(base, sequence);
    }
}
