package faang.school.accountservice.service;

import faang.school.accountservice.exception.AbsentFreeAccException;
import faang.school.accountservice.exception.CreateNewFreeAccNumException;
import faang.school.accountservice.model.AccountBalanceType;
import faang.school.accountservice.model.AccountNumberSequence;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {
    @Value("${debit.pattern}")
    private BigInteger debitPattern;
    @Value("${credit.pattern}")
    private BigInteger creditPattern;

    private final AccountNumbersSequenceRepository sequenceRepository;
    private final FreeAccountNumbersRepository freeNumbersRepository;

    @Transactional(rollbackFor = CreateNewFreeAccNumException.class)
    public void createOneFreeAccNumberPerType(AccountBalanceType type) {
        log.info("Starting to create a new free account number for type: {}", type);
        if (sequenceRepository.getIncrementedCountByBalanceType(type) == null) {
            createNewSeqAndAccountNum(type);
            return;
        }
        sequenceRepository.incrementCountByBalanceType(type);
        createNewFreeAccountNumber(type);
    }

    @Transactional(rollbackFor = AbsentFreeAccException.class)
    public FreeAccountNumber getFreeAccNumberByType(AccountBalanceType type) {
        log.info("Starting to get a free account number for type: {}", type);
        FreeAccountNumber freeAccountNumber = freeNumbersRepository.findRandomByAccountBalanceType(type);

        if (freeAccountNumber == null) {
            log.warn("No sequence found for type: {}, creating a new one.", type);
            throw new AbsentFreeAccException("No free account numbers available for type: " + type);
        }
        log.info("Found free account number: {}", freeAccountNumber.getAccountNumber());
        freeNumbersRepository.delete(freeAccountNumber);
        return freeAccountNumber;
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    public void createQuantityOfNewAccNumbers(AccountBalanceType type, Integer quantity) {
        log.info("Starting to create {} new account numbers for type: {}", quantity, type);

        Integer finalQuantityToCreate = quantity;
        if (quantity <= 0) {
            log.error("Invalid quantity: {}. Must be greater than 0", quantity);
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (sequenceRepository.getIncrementedCountByBalanceType(type) == null) {
            createNewSeqAndAccountNum(type);
            finalQuantityToCreate -= 1;
        }
        if (quantity == 1) {
            log.info("Creating one new account number for type: {}", type);
            sequenceRepository.incrementCountByBalanceType(type);
            createNewFreeAccountNumber(type);
            return;
        }
        incrementSeqAndCreateBatch(type, finalQuantityToCreate);
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    public void createTargetQuantityOfAccNumbers(AccountBalanceType type, Integer targetQuantity) {
        log.info("Starting to create target quantity of account numbers for type: {}, target: {}",
                type, targetQuantity);

        if (targetQuantity <= 0) {
            log.error("Invalid quantity: {}. Must be greater than 0", targetQuantity);
            throw new IllegalArgumentException("Target quantity must be greater than 0");
        }
        if (sequenceRepository.getIncrementedCountByBalanceType(type) == null) {
            createNewSeqAndAccountNum(type);
        }

        Integer actualFreeAccountCount = freeNumbersRepository.getActualFreeNumCountByType(type);
        if (targetQuantity - actualFreeAccountCount <= 0) {
            log.info("Target quantity already met or exceeded for type: {}, current count: {}",
                    type, actualFreeAccountCount);
            return;
        } else if (targetQuantity - actualFreeAccountCount == 1) {
            log.info("Creating one new account number for type: {}", type);
            sequenceRepository.incrementCountByBalanceType(type);
            createNewFreeAccountNumber(type);
            return;
        }

        incrementSeqAndCreateBatch(type, targetQuantity - actualFreeAccountCount);
    }

    private void createNewSequenceByType(AccountBalanceType type) {
        log.info("Starting creating a new sequence for type: {}", type);
        AccountNumberSequence sequence = new AccountNumberSequence();
        sequence.setAccountBalanceType(type);
        sequence.setAcountsCount(0);
        sequenceRepository.save(sequence);
    }

    private void createNewFreeAccountNumber(AccountBalanceType type) {
        log.info("Creating new free account number for type: {}", type);
        BigInteger accountNum = getPatternByType(type)
                .add(BigInteger.valueOf(sequenceRepository.getIncrementedCountByBalanceType(type)));

        freeNumbersRepository.save(createObjFreeAccNum(type, accountNum));
    }

    private void createFreeAccountNumbersBatch(AccountBalanceType type, Integer startSequence, Integer quantity) {
        log.info("Creating batch of {} account numbers starting from sequence {}", quantity, startSequence);
        List<FreeAccountNumber> freeAccountNumbers = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            BigInteger accountNum = getPatternByType(type).add(BigInteger.valueOf(startSequence++));
            freeAccountNumbers.add(createObjFreeAccNum(type, accountNum));
        }
        freeNumbersRepository.saveAll(freeAccountNumbers);
    }

    private BigInteger getPatternByType(AccountBalanceType type) {
        log.info("Selecting selecting pattern for type: {}", type);
        if (type == AccountBalanceType.DEBIT) {
            return debitPattern;
        } else if (type == AccountBalanceType.CREDIT) {
            return creditPattern;
        }
        throw new IllegalArgumentException("Unknown account balance type: " + type);
    }

    private FreeAccountNumber createObjFreeAccNum(AccountBalanceType type, BigInteger accountNum) {
        FreeAccountNumber freeAccountNumber = new FreeAccountNumber();
        freeAccountNumber.setAccountBalanceType(type);
        freeAccountNumber.setAccountNumber(String.valueOf(accountNum));
        return freeAccountNumber;
    }

    private void incrementSeqAndCreateBatch(AccountBalanceType type, Integer quantity) {
        log.info("Incrementing sequence and creating batch for type: {}, quantity: {}", type, quantity);

        Integer currentCount = sequenceRepository.getIncrementedCountByBalanceType(type);
        if (currentCount == null) {
            log.warn("Creating new sequence for type: {}", currentCount);
            createNewSequenceByType(type);
        }
        Integer incrementedCurrentCount = currentCount + 1;
        sequenceRepository.incrementCountByTypeWithQuantity(type, quantity);
        createFreeAccountNumbersBatch(type, incrementedCurrentCount, quantity);
        log.info("Successfully created {} account numbers for type: {}", quantity, type);
    }

    private void createNewSeqAndAccountNum(AccountBalanceType type) {
        log.info("Starting a new sequence for type: {}", type);
        createNewSequenceByType(type);
        sequenceRepository.incrementCountByBalanceType(type);
        createNewFreeAccountNumber(type);
    }
}
