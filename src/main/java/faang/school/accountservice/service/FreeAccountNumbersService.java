package faang.school.accountservice.service;

import faang.school.accountservice.entity.account.account_number.AccountNumbersSequence;
import faang.school.accountservice.entity.account.account_number.FreeAccountNumber;
import faang.school.accountservice.entity.account.account_number.FreeAccountNumberId;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {

    private static final long DEBIT_ACCOUNT_PATTERN = 4200_0000_0000_0000L;
    private static final long SAVINGS_ACCOUNT_PATTERN = 5236_0000_0000_0000L;

    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Transactional
    public void generateFreeAccountNumbers(AccountType accountType, int batchSize) {
        long accountPattern = accountType == AccountType.DEBIT
                ? DEBIT_ACCOUNT_PATTERN : SAVINGS_ACCOUNT_PATTERN;
        AccountNumbersSequence sequence = accountNumbersSequenceRepository.incrementCounter(
                accountType, batchSize);
        List<FreeAccountNumber> accountNumbers = new ArrayList<>();

        for (long i = sequence.getInitialValue(); i < sequence.getCounter(); i++) {
            accountNumbers.add(new FreeAccountNumber(
                    new FreeAccountNumberId(accountType,
                            accountPattern + i)));
        }

        freeAccountNumbersRepository.saveAll(accountNumbers);
    }

    @Transactional
    public void retrieveAccountNumber(AccountType accountType,
                                      Consumer<FreeAccountNumber> numberConsumer) {
        numberConsumer.accept(freeAccountNumbersRepository.retrieveFirst(
                accountType));
    }
}
