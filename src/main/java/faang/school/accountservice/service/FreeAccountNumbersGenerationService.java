package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.LongStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeAccountNumbersGenerationService {
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final FreeAccountNumbersService freeAccountNumbersService;

    @Transactional
    public void generateNewAccounts(AccountType accountType, long quantity) {
        long currentQuantity = freeAccountNumbersRepository.countByAccountType(accountType);

        long requiredQuantity = quantity - currentQuantity;
        for (long i = 0; i < requiredQuantity; i++) {
            freeAccountNumbersService.generateNewAccountNumberByType(accountType);
        }
    }
}
