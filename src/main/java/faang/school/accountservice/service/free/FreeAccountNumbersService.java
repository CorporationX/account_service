package faang.school.accountservice.service.free;

import faang.school.accountservice.entity.account.AccountNumbersSequence;
import faang.school.accountservice.entity.free.FreeAccountNumber;
import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.repository.account.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.free.FreeAccountNumbersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;


@Slf4j
@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {

    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Transactional
    public void generateAccountNumbers() {
        return; // заглушка
    }

    @Transactional
    public void retrieveAccountNumber(AccountType type, Consumer<FreeAccountNumber> numberConsumer) {
        numberConsumer.accept(freeAccountNumbersRepository.retrieveFirst(type.name()));
    }

    public AccountNumbersSequence incrementAndGetCounter(String type, int batchSize) {
        return accountNumbersSequenceRepository.incrementAndGetCounter(type, batchSize);
    }

    public void saveAllAccountNumbers(List<FreeAccountNumber> numbers) {
        freeAccountNumbersRepository.saveAll(numbers);
    }
}
