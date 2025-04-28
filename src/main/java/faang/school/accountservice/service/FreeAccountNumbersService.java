package faang.school.accountservice.service;

import faang.school.accountservice.dto.FreeAccountDto;
import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.entity.FreeAccountNumberId;
import faang.school.accountservice.enums.CardType;
import faang.school.accountservice.exception.FreeAccountNumberException;
import faang.school.accountservice.exception.InvalidBatchSizeException;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeAccountNumbersService {
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final Object lock = new Object();

    public void initCards() {
        List<AccountNumberSequence> notExistedAccounts = Arrays.stream(CardType.values())
                .filter(cardType ->
                        !accountNumbersSequenceRepository.existsAccountNumberSequenceByType(cardType))
                .map(cardType -> new AccountNumberSequence(cardType, 0))
                .toList();

        if (!notExistedAccounts.isEmpty()) {
            List<AccountNumberSequence> createdAccounts =
                    accountNumbersSequenceRepository.saveAll(notExistedAccounts);

            log.debug("New card types initialised: {}",
                    createdAccounts.stream().map(AccountNumberSequence::getType).toList());
        } else {
            log.debug("All card type already initialised");
        }
    }

    @Transactional
    public void generateAccountNumbersForType(@NotNull CardType type, int batchSize) {
        log.debug("Start generating accountNumbers for card type {}", type);

        if (batchSize <= 0) {
            throw new InvalidBatchSizeException("Batch size must be greater than 0");
        }

        AccountNumberSequence numberSequence = accountNumbersSequenceRepository.incrementAndGet(type.name(), batchSize);

        long currentSequenceNumber = numberSequence.getCount();
        long previousSequenceNumber = currentSequenceNumber - batchSize;

        log.debug("Card will be generated from: {} to: {}", previousSequenceNumber, currentSequenceNumber);
        freeAccountNumbersRepository.saveAll(
                generateAccountNumbersForType(type, previousSequenceNumber, currentSequenceNumber));

        log.debug("Finished generating accountNumbers for card type {}", type);
    }


    @Transactional
    public FreeAccountDto getFreeAccountForType(@NotNull CardType type,
                                                @NotNull Function<FreeAccountNumber, FreeAccountDto> function) {
        synchronized (lock) {
            return function.apply(freeAccountNumbersRepository.getFreeAccount(type.name())
                    .orElseGet(() -> {
                        generateAccountNumbersForType(type, 1);
                        return freeAccountNumbersRepository.getFreeAccount(type.name())
                                .orElseThrow(() ->
                                        new FreeAccountNumberException("Can't generate card number for type " + type));
                    }));
        }
    }

    private List<FreeAccountNumber> generateAccountNumbersForType(@NotNull CardType type, long from, long to) {
        List<FreeAccountNumber> freeAccountNumbers = new ArrayList<>();
        for (long i = from; i < to; i++) {
            freeAccountNumbers.add(
                    new FreeAccountNumber(new FreeAccountNumberId(type, type.getCardPattern() + i))
            );
        }
        return freeAccountNumbers;
    }
}
