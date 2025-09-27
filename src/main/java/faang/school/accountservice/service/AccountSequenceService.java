package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountSequenceService {

    private final AccountNumbersSequenceRepository repository;

    @Transactional(readOnly = true)
    public Long getCurrentSequence(AccountType type) {
        return repository.findById(type)
                .map(AccountNumbersSequence::getCurrentSequence)
                .orElse(0L);
    }

    @Transactional
    public AccountNumbersSequence initialize(AccountType type) {
        log.info("Initializing sequence for type {}", type);
        return repository.createSequenceForType(type);
    }

    public boolean canGenerate(AccountType type, int count) {
        Long current = getCurrentSequence(type);
        long max = type.getMaxSequenceValue();
        return current + count <= max;
    }
}