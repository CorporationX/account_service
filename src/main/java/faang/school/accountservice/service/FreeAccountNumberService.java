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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
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

    private final AccountSeqRepository accountSeqRepository;
    private final FreeAccountRepository freeAccountRepository;

    @Transactional
    public void generateAccountNumbers(AccountType type, int batchSize) {
        try{
            long prefix = ACCOUNT_PREFIXES.getOrDefault(type, 0L);
            if (prefix == 0L) {
                throw new InvalidAccountTypeException("Неопознанный тип счета" + type);
            }
            AccountSeq period = accountSeqRepository.incrementCounter(type.name(), batchSize);
            List<FreeAccountNumber> numbers = new ArrayList<>();
            for (long i = period.getInitialValue(); i < period.getCounter(); i++) {
                numbers.add(new FreeAccountNumber(new FreeAccountId(type, prefix + i)));
            }
            freeAccountRepository.saveAll(numbers);
        } catch (InvalidAccountTypeException e) {
            System.out.println(e.getMessage());
        }
    }

    @Transactional
    public void retrieveFreeAccountNumber(AccountType accountType, Consumer<FreeAccountNumber> numberConsumer) {
        try {
            FreeAccountNumber freeAccountNumber = freeAccountRepository.retrieveFirst(accountType.name());

            if (freeAccountNumber == null) {
                throw new NoFreeAccountNumbersException("Нет доступных свободных номеров для типа счета: " + accountType);
            }

            numberConsumer.accept(freeAccountNumber);

            AccountSeq accountSeq = accountSeqRepository.incrementCounter(accountType.name(), 1);

            if (accountSeq == null) {
                accountSeq = new AccountSeq();
                accountSeq.setType(accountType);
                accountSeq.setCounter(1);
                accountSeqRepository.save(accountSeq);
            }
        } catch (NoFreeAccountNumbersException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении свободного номера счета", e);
        }
    }

}
