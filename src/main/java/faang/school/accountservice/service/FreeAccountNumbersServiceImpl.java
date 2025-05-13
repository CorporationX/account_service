package faang.school.accountservice.service;

import faang.school.accountservice.config.AccountNumberProperties;
import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.entity.FreeAccountNumberId;
import faang.school.accountservice.enums.AccountNumberType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.LongStream;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "app.service.free-account-numbers",
        havingValue = "default",
        matchIfMissing = true
)
public class FreeAccountNumbersServiceImpl implements FreeAccountNumbersService {

    private static final long ACCOUNT_NUMBER_MULTIPLIER = 1_000_000_000_000L;

    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumberProperties accountNumberProperties;

    @Value("${account.number.batch.fallback-size}")
    private int fallbackBatchSize;

    @Override
    @Transactional
    public void generateAccountNumbers(AccountNumberType type, int batchSize) {
        log.info("Starting generate '{}' account numbers...", type);
        Integer numberPrefix = accountNumberProperties.getPrefixes().get(type);

        if (numberPrefix == null) {
            throwWithLogging(new IllegalArgumentException(String.format("Unknown account number type: %s", type)));
        }

        Optional<AccountNumbersSequence> sequence = accountNumbersSequenceRepository.findByTypeForUpdate(type);
        sequence.ifPresentOrElse(
                accountNumbersSequence -> {
                    long initialCounterValue = accountNumbersSequence.getCounter();
                    long finalCounterValue = initialCounterValue + batchSize;
                    accountNumbersSequence.setCounter(finalCounterValue);

                    List<FreeAccountNumber> freeAccountNumbers = LongStream.range(initialCounterValue, finalCounterValue)
                            .mapToObj(num -> createAccountNumber(type, numberPrefix, num))
                            .toList();

                    freeAccountNumbersRepository.saveAll(freeAccountNumbers);
                    log.info("Generating successfully '{}' account numbers", type);
                },
                () -> throwWithLogging(new IllegalArgumentException(
                        String.format("Sequence not found by '%s' type", type))
                )
        );
    }

    @Override
    @Transactional
    public void receiveAccountNumber(AccountNumberType type, Consumer<FreeAccountNumber> numberConsumer) {
        log.info("Starting receive '{}' account number...", type);
        Optional<FreeAccountNumber> accountNumber = freeAccountNumbersRepository.findFirstByTypeForUpdate(type.name());

        if (accountNumber.isEmpty()) {
            accountNumbersSequenceRepository.findByTypeForUpdate(type);
            accountNumber = freeAccountNumbersRepository.findFirstByTypeForUpdate(type.name());

            if (accountNumber.isEmpty()) {
                generateAccountNumbers(type, fallbackBatchSize);
                accountNumber = freeAccountNumbersRepository.findFirstByTypeForUpdate(type.name());
            }
        }

        accountNumber.ifPresentOrElse(
                number -> {
                    freeAccountNumbersRepository.delete(number);
                    log.info("Receiving successfully '{}' account number", type);

                    numberConsumer.accept(number);
                },
                () -> throwWithLogging(new IllegalStateException("Failed to obtain account number after generation"))
        );
    }

    private FreeAccountNumber buildFreeAccountNumber(AccountNumberType type, long accountNumber) {
        return FreeAccountNumber.builder()
                .id(FreeAccountNumberId.builder()
                        .type(type)
                        .accountNumber(accountNumber)
                        .build()
                )
                .build();
    }

    private FreeAccountNumber createAccountNumber(AccountNumberType type, Integer numberPrefix, long number) {
        long accountNumber = numberPrefix * ACCOUNT_NUMBER_MULTIPLIER + number;
        log.debug("Generated '{}' account number: {}", type, accountNumber);

        return buildFreeAccountNumber(type, accountNumber);
    }

    private void throwWithLogging(RuntimeException exception) {
        log.error(exception.getMessage());
        throw exception;
    }
}
