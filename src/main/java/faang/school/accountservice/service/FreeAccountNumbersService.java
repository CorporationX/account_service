package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {

    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Value("${account.number.length}")
    private int numberLength;

    public String createNewFreeNumber(AccountType accountType) {
        String typeName = accountType.name();
        log.info("Create new free number for Account Type: {}", typeName);
        Long currentCounter = accountNumbersSequenceRepository.getCurrentCounter(typeName);

        if (currentCounter == null) {
            log.info("Counter for AccountType: {} not exists. Creates one", typeName);
            accountNumbersSequenceRepository.createCounterForType(typeName);
            currentCounter = 0L;
        }

        Long newCounter = null;
        while (newCounter == null) {
            newCounter = accountNumbersSequenceRepository.incrementCounter(currentCounter, typeName);
        }

        String number = initNumber(accountType.getPrefix(), newCounter);
        freeAccountNumbersRepository.createFreeNumber(number, typeName);
        return number;
    }

    private String initNumber(String prefix, long counter) {
        StringBuilder sb = new StringBuilder(prefix);
        String strCounter = String.valueOf(counter);
        while (sb.length() + strCounter.length() != numberLength) {
            sb.append(0);
        }
        sb.append(strCounter);
        return sb.toString();
    }

    @Transactional
    public String getFreeNumber(AccountType accountType) {
        String number = freeAccountNumbersRepository.getFirstFreeNumber(accountType.name());
        if (number == null) {
            number = createNewFreeNumber(accountType);
        }
        freeAccountNumbersRepository.deleteByNumber(number);
        return number;
    }
}
