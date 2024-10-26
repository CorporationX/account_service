package faang.school.accountservice.service;

import faang.school.accountservice.dto.FreeAccountNumberDto;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {

    private final FreeAccountNumberRepository freeAccountNumberRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Transactional
    public void saveNewAccountNumber(Long accountNumber, String accountType) {
        FreeAccountNumber newAccountNumber = new FreeAccountNumber();
        newAccountNumber.setAccountNumber(accountNumber);
        newAccountNumber.setAccountType(AccountType.valueOf(accountType));
        freeAccountNumberRepository.save(newAccountNumber);
    }

    @Transactional
    public void saveNewAccountNumbers(List<Long> accountNumbers, List<String> accountTypes) {
        for (Long accountNumber : accountNumbers) {
            for (String accountType : accountTypes) {
                saveNewAccountNumber(accountNumber, accountType);
            }
        }
    }

    @Transactional
    public void receivingAndDeletingAccountNumber(FreeAccountNumberDto accountNumberDto, Consumer<Long> action) {
        String accountType = accountNumberDto.getAccountType();
        Optional<FreeAccountNumber> freeAccountNumber = freeAccountNumberRepository
                .findAndDeleteFirstFreeAccountNumberByType(accountType);

        long accountNumber = freeAccountNumber.get().getAccountNumber();

        if (freeAccountNumber.isPresent()) {
            action.accept(accountNumber);
        } else {
            Optional<Long> currentCounter = accountNumbersSequenceRepository.findCurrentCounterByType(accountType);
            if (currentCounter.isEmpty()) {
                accountNumbersSequenceRepository.createCounter(accountType);
            }

            accountNumbersSequenceRepository.incrementCounter(accountType, currentCounter.get());
            // получение нового номера счета,
            // думаю что метод должен реализоваться в задаче генерация уникальных номеров счетов
            action.accept(accountNumber);
        }
    }

}
