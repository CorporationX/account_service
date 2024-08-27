package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeAccountNumbersGenerationService {
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final FreeAccountNumbersService freeAccountNumbersService;

    @Transactional
    public void generateNewAccounts(AccountType accountType, long quantity) {
        for (long i = 0; i < quantity; i++) {
            freeAccountNumbersService.generateNewAccountNumberByType(accountType);
        }
    }

    @Transactional
    public void generateAccountsUpToLimit(AccountType accountType, long limit) {
        long currentQuantity = freeAccountNumbersRepository.countByAccountType(accountType);
        long requiredQuantity = limit - currentQuantity;
        if (requiredQuantity > 0) {
            generateNewAccounts(accountType, requiredQuantity);
        }
    }
}
