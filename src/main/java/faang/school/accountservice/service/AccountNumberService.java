package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.model.AccTypeFreeNumberId;
import faang.school.accountservice.repository.AccNumSequenceRepository;
import faang.school.accountservice.repository.FreeAccNumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountNumberService {
    private final AccNumSequenceRepository accNumSequenceRepository;
    private final FreeAccNumRepository freeAccNumRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generateAccountNumber(AccountType type, long batchSize) {
        accNumSequenceRepository.createCounterIfNotExist(type.name());
        List<FreeAccountNumber> freeAccountNumbers = new ArrayList<>();
        AccountNumbersSequence sequence = accNumSequenceRepository.increaseCounter(type.name(), batchSize);
        log.info("the counter for type {} was increased", type.name());
        long initialCount = sequence.getCounter() - batchSize;
        for (long i = initialCount; i < sequence.getCounter(); i++) {
            freeAccountNumbers.add(new FreeAccountNumber(new AccTypeFreeNumberId(type, type.getPattern() + i)));
        }
        freeAccNumRepository.saveAll(freeAccountNumbers);
        log.info("a free number for type {} was saved", type.name());
    }

    @Transactional()
    public FreeAccountNumber getFreeAccNumAndProcess(AccountType type, Consumer<FreeAccountNumber> consumer) {
        FreeAccountNumber number = freeAccNumRepository.retrieveFreeAccNum(type.name());
        if (number == null) {
            log.info("free number for type {} was not found and will be generated", type.name());
            generateAccountNumber(type, 1);
            number = freeAccNumRepository.retrieveFreeAccNum(type.name());
            log.info("free number for type {} was created", type.name());
        }
        consumer.accept(number);
        log.info("additional functionality over received number {} for type {} was processed", number.getId().getFreeNumber(), number.getId().getAccTypeFreeNumber().name());
        return number;
    }
}
