package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountSequence;
import faang.school.accountservice.entity.AccountType;
import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.repository.AccountSequenseRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeAccountNumbersService {
    private final AccountSequenseRepository accountSequenseRepository;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;

    private static final long ACCOUNT_PATTERN = 4200_0000_0000_0000L;

    @Transactional
    public void generateAccountNumbers(@NotNull AccountType type, int butchSize) {
        log.info("Generating account numbers for type: {}, butchSize: {}", type, butchSize);
        AccountSequence next = accountSequenseRepository.incrementCounter(type.name(), butchSize);
        log.info("Generated account numbers: {}", next);
        List<FreeAccountNumber> accountNumbers = new ArrayList<>();
        for (long i = next.getInitialCounter(); i < next.getCounter(); i++) {
            FreeAccountId accountId = new FreeAccountId(type, ACCOUNT_PATTERN + i);
            accountNumbers.add(new FreeAccountNumber(accountId));
        }
        if (accountNumbers.isEmpty()) {
            log.warn("No account numbers generated");
            return;
        }
        freeAccountNumbersRepository.saveAll(accountNumbers);
        log.info("Saved account numbers: {}", accountNumbers);
    }

    @Transactional
    public void retrieveAccountNumber(@NotNull AccountType type, Consumer<FreeAccountNumber> consumer) {
        log.info("Retrieving account number for type: {}", type);
        FreeAccountNumber accountNumber = freeAccountNumbersRepository.retreiveFirst(type.name());
        log.info("Retrieved account number: {}", accountNumber);
        consumer.accept(accountNumber);
        log.info("Consumed account number: {}", accountNumber);
    }
}
