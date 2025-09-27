package faang.school.accountservice.service;

import faang.school.accountservice.config.context.AccountGenerationConfig;
import faang.school.accountservice.entity.FreeAccountNumbers;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountNumberBatchProcessor {

    private final AccountGenerationConfig config;
    private final AccountNumberGenerator generator;
    private final FreeAccountNumberPoolService poolService;
    private final FreeAccountNumbersRepository repository;


    @Transactional
    public List<FreeAccountNumbers> generateAndAddToPool(
            AccountType type, int count) {

        List<FreeAccountNumbers> result = new ArrayList<>();
        Set<String> existingNumbers = loadExistingNumbers(type);

        for (int attempt = 0; attempt < config.getMaxDuplicateRetries()
                && result.size() < count; attempt++) {

            int remaining = count - result.size();
            List<String> batch = generator.generateBatch(type, remaining);

            List<FreeAccountNumbers> unique = batch.stream()
                    .filter(num -> !existingNumbers.contains(num))
                    .map(num -> new FreeAccountNumbers(type, num))
                    .toList();

            List<FreeAccountNumbers> saved = saveWithDuplicateHandling(unique);
            result.addAll(saved);

            saved.forEach(n -> existingNumbers.add(n.getAccountNumber()));
        }

        return result;
    }

    private Set<String> loadExistingNumbers(AccountType type) {
        try {
            return new HashSet<>(repository.findAll().stream()
                    .filter(n -> n.getAccountType() == type)
                    .map(FreeAccountNumbers::getAccountNumber)
                    .toList());
        } catch (Exception e) {
            log.warn("Failed to load existing numbers: {}", e.getMessage());
            return new HashSet<>();
        }
    }

    private List<FreeAccountNumbers> saveWithDuplicateHandling(
            List<FreeAccountNumbers> numbers) {
        try {
            return repository.saveAll(numbers);
        } catch (DataIntegrityViolationException e) {
            return saveIndividually(numbers);
        }
    }

    private List<FreeAccountNumbers> saveIndividually(
            List<FreeAccountNumbers> numbers) {
        List<FreeAccountNumbers> saved = new ArrayList<>();

        for (FreeAccountNumbers number : numbers) {
            try {
                saved.add(repository.save(number));
            } catch (DataIntegrityViolationException e) {
                log.debug("Skipping duplicate: {}", number.getAccountNumber());
            }
        }

        return saved;
    }
}