package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.NoAvailableAccountNumberException;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import faang.school.accountservice.config.context.AccountGenerationConfig;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountNumberGenerator {

    private final AccountGenerationConfig config;
    private final AccountNumbersSequenceRepository sequenceRepository;

    public String generateAccountNumber(AccountType accountType) {
        Long sequence = sequenceRepository.getNextSequenceValue(
                accountType, config.getMaxRetryAttempts());

        if (!accountType.isSequenceValid(sequence)) {
            throw new NoAvailableAccountNumberException(
                    String.format("Sequence %d exceeds maximum for type %s",
                            sequence, accountType));
        }

        return accountType.generateAccountNumber(sequence);
    }

    public List<String> generateBatch(AccountType accountType, int count) {
        Long startSequence = sequenceRepository.reserveSequenceBlock(
                accountType, count, config.getMaxRetryAttempts());

        return IntStream.range(0, count)
                .mapToObj(i -> accountType.generateAccountNumber(startSequence + i))
                .toList();
    }
}
