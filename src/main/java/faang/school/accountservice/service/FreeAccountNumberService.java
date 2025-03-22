package faang.school.accountservice.service;

import faang.school.accountservice.exception.InvalidAccountTypeException;
import faang.school.accountservice.exception.NoFreeAccountNumbersException;
import faang.school.accountservice.model.account.AccountSeq;
import faang.school.accountservice.model.account.FreeAccountId;
import faang.school.accountservice.model.account.FreeAccountNumber;
import faang.school.accountservice.model.account.enums.AccountType;
import faang.school.accountservice.repository.AccountSeqRepository;
import faang.school.accountservice.repository.FreeAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeAccountNumberService {
    private static final Map<AccountType, Long> ACCOUNT_PREFIXES = Map.of(
            AccountType.CURRENT_INDIVIDUAL, 4200_0000_0000_0000L,
            AccountType.CURRENT_BUSINESS, 4300_0000_0000_0000L,
            AccountType.SAVINGS, 4400_0000_0000_0000L,
            AccountType.CURRENCY, 4500_0000_0000_0000L,
            AccountType.INVESTMENT, 4600_0000_0000_0000L,
            AccountType.CREDIT, 4700_0000_0000_0000L,
            AccountType.DEPOSIT, 4800_0000_0000_0000L,
            AccountType.ESCROW, 4900_0000_0000_0000L,
            AccountType.JOINT, 5000_0000_0000_0000L,
            AccountType.TRUST, 5100_0000_0000_0000L
    );

    private static final int DEFAULT_BATCH_SIZE = 10; // Количество номеров для генерации при нехватке

    private final AccountSeqRepository accountSeqRepository;
    private final FreeAccountRepository freeAccountRepository;

    @Transactional
    public void generateAccountNumbers(AccountType type, int batchSize) {
        if (!ACCOUNT_PREFIXES.containsKey(type)) {
            throw new InvalidAccountTypeException("Неопознанный тип счета: " + type);
        }
        long prefix = ACCOUNT_PREFIXES.get(type);
        List<FreeAccountNumber> numbers = new ArrayList<>();
        for (long i = 0; i < batchSize; i++) {
            Long counterValue = accountSeqRepository.getNextCounterValue();
            long accountNumber = prefix + counterValue;
            numbers.add(new FreeAccountNumber(new FreeAccountId(type, accountNumber)));
        }
        freeAccountRepository.saveAll(numbers);
        log.info("Сгенерировано {} новых номеров для типа счета {}", batchSize, type);
    }

    @Transactional
    public void retrieveFreeAccountNumber(AccountType accountType, Consumer<FreeAccountNumber> numberConsumer) {
        FreeAccountNumber freeAccountNumber = freeAccountRepository.findFirst(accountType.name());

        if (freeAccountNumber == null) {
            log.warn("Нет доступных свободных номеров для типа счета: {}. Запускаем генерацию...", accountType);
            generateAccountNumbers(accountType, DEFAULT_BATCH_SIZE);
            freeAccountNumber = freeAccountRepository.findFirst(accountType.name());

            if (freeAccountNumber == null) {
                throw new NoFreeAccountNumbersException("Ошибка: после генерации номеров они не появились.");
            }
        }

        freeAccountRepository.deleteByAccountTypeAndAccountNumber(accountType.name(),
                freeAccountNumber.getId().getAccountNumber());

        numberConsumer.accept(freeAccountNumber);

        AccountSeq accountSeq = accountSeqRepository.findByType(accountType);
        if (accountSeq == null) {
            accountSeq = new AccountSeq();
            accountSeq.setType(accountType);
            accountSeq.setCounter(1);
            accountSeqRepository.save(accountSeq);
        }
    }
}
