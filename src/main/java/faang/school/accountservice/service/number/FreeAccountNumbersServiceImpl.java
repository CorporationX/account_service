package faang.school.accountservice.service.number;

import faang.school.accountservice.config.AccountNumberProperties;
import faang.school.accountservice.entity.account.FreeAccountId;
import faang.school.accountservice.entity.account.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersServiceImpl {

    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountSequenceService accountSequenceService;
    private final AccountNumberProperties accountNumberProperties;

    @Transactional
    public void generateAccountNumbers(AccountType type, int batchSize) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be > 0");
        }

        AccountPeriod period =
                accountSequenceService.incrementCounter(type, batchSize);

        List<FreeAccountNumber> freeAccountNumbers = new ArrayList<>();
        for (long seq = period.fromInclusive(); seq <= period.toInclusive(); seq++) {
            freeAccountNumbers.add(createFreeAccountNumber(type, seq));
        }

        freeAccountNumbersRepository.saveAll(freeAccountNumbers);
    }

    private FreeAccountNumber createFreeAccountNumber(AccountType type, long sequenceNumber) {
        String accountNumber = buildAccountNumber(type, sequenceNumber);
        FreeAccountId id = new FreeAccountId(type, accountNumber);
        return new FreeAccountNumber(id);
    }

    private long getPrefix(AccountType type) {
        Long prefix = accountNumberProperties.getPrefix().get(type);
        if (prefix == null) {
            throw new IllegalStateException("Prefix not configured for account type: " + type);
        }
        return prefix;
    }

    private String buildAccountNumber(AccountType type, long sequenceNumber) {
        long full = getPrefix(type) + sequenceNumber;
        return Long.toString(full);
    }

    @Transactional
    public void retrieveAccountNumber(AccountType type, Consumer<String> consumer) {
        String accountNumber = getOrGenerateAccountNumber(type);
        consumer.accept(accountNumber);
    }

    private String getOrGenerateAccountNumber(AccountType type) {
        FreeAccountNumber free = freeAccountNumbersRepository.findFirstForUpdate(type.name());

        if (free != null) {
            String number = free.getId().getAccountNumber();
            freeAccountNumbersRepository.deleteByTypeAndAccountNumber(type.name(), number);
            return number;
        }

        AccountPeriod period = accountSequenceService.incrementCounter(type, 1);
        long seq = period.fromInclusive();
        return buildAccountNumber(type, seq);
    }
}
