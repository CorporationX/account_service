package faang.school.accountservice.service.account.numbers.sequence;

import faang.school.accountservice.enums.AccountNumberType;
import faang.school.accountservice.model.number.AccountUniqueNumberCounter;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class DigitSequenceManager {
    private final DigitSequenceGenerator digitSequenceGenerator;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Async
    @Transactional
    public void tryStartGenerationNumberForPool(AccountNumberType type) {
        if (!permissionToStartGeneration(type)) {
            log.warn("Generation for type {} will be skipped because another thread is already generating.", type);
            return;
        }
        if (!tryLockCounter(type)) {
            log.warn("No locked counter found for type {}. Generation will be skipped.", type);
            return;
        }
        startGeneration(type);

    }

    public Optional<String> getAndRemoveFreeAccountNumberByType(AccountNumberType type) {
        return digitSequenceGenerator.getAndRemoveFreeAccountNumberByType(type);
    }

    public String generateNewAccountNumberWithoutPool(AccountNumberType type) {
        return digitSequenceGenerator.generateNewAccountNumberWithoutPool(type);
    }


    private void startGeneration(AccountNumberType type) {
        accountNumbersSequenceRepository.setActiveGenerationState(type.toString());
        digitSequenceGenerator.generationSequencesAsync(type);
    }

    private boolean permissionToStartGeneration(AccountNumberType type) {
        Optional<AccountUniqueNumberCounter> counterOpt = accountNumbersSequenceRepository.findAccountUniqueNumberCounterByType(type.toString());
        return counterOpt.filter(AccountUniqueNumberCounter::permissionToStartGeneration).isPresent();
    }

    private boolean tryLockCounter(AccountNumberType type) {
        Optional<AccountUniqueNumberCounter> lockedCounterOpt =
                accountNumbersSequenceRepository.tryLockCounterByTypeForUpdate(type.toString());
        return lockedCounterOpt.isPresent();
    }

}
