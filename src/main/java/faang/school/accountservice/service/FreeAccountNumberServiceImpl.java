package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.account.AccountNumberSequenceRepository;
import faang.school.accountservice.repository.account.FreeAccountNumberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreeAccountNumberServiceImpl implements FreeAccountNumberService {

    private final FreeAccountNumberRepository freeAccountNumberRepository;
    private final AccountNumberSequenceRepository accountNumberSequenceRepository;

    @Transactional
    @Override
    public BigInteger getFreeAccountNumber(AccountType accountType) {
        Optional<BigInteger> accountNumberOpt = freeAccountNumberRepository.findFirstFreeAccountNumber(accountType.name());
        accountNumberOpt.ifPresent(freeAccountNumberRepository::deleteFreeAccountNumberByAccountNumber);
        return accountNumberOpt.orElseGet(() -> generateFreeAccountNumber(accountType));
    }

    private BigInteger generateFreeAccountNumber(AccountType accountType) {
        String code = accountType.getValue();
        accountNumberSequenceRepository.incrementSequence(accountType.name(), 0L);
        Optional<Long> currentValueOpt = accountNumberSequenceRepository.getCurrentValue(accountType.name());
        if (currentValueOpt.isEmpty()) {
            accountNumberSequenceRepository.createSequence(accountType.name(), 0L);
            accountNumberSequenceRepository.incrementSequence(accountType.name(), 0L);
        } else {
            accountNumberSequenceRepository.incrementSequence(accountType.name(), currentValueOpt.get());
        }
        long currentValue = accountNumberSequenceRepository.getCurrentValue(accountType.name())
                .orElseThrow(() -> new RuntimeException("Failed to generate account number"));
        return new BigInteger(code + String.format("%016d", currentValue));
    }
}
