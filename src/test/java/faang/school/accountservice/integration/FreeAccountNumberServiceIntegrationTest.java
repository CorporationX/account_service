package faang.school.accountservice.integration;

import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.integration.config.RedisPostgresTestContainers;
import faang.school.accountservice.mapper.FreeAccountNumberMapper;
import faang.school.accountservice.model.AccountNumbersSequence;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.service.FreeAccountNumberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import static faang.school.accountservice.enums.account.AccountType.CREDIT;
import static faang.school.accountservice.enums.account.AccountType.DEBIT;
import static faang.school.accountservice.enums.account.AccountType.SAVINGS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@EnableRetry
class FreeAccountNumberServiceIntegrationTest extends RedisPostgresTestContainers {
    @Autowired
    private FreeAccountNumberService freeAccountNumberService;

    @Autowired
    private FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Autowired
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Autowired
    private FreeAccountNumberMapper freeAccountNumberMapper;

    private final String number = "5536000000000000";
    private final int accountNumberLength = 16;
    private final int typeCodeLength = 4;
    private final int batchSize = 5;

    @AfterEach
    public void cleanUp() {
        freeAccountNumbersRepository.deleteAll();
        List<AccountNumbersSequence> sequences = accountNumbersSequenceRepository.findAll();
        sequences.stream()
                .peek(sequence -> sequence.setCurrentNumber(0L))
                .forEach(accountNumbersSequenceRepository::save);
    }

    @Test
    void testGetFreeAccountNumberFromWhenFreeAccountNumberExists() {
        FreeAccountNumber freeAccountNumber = FreeAccountNumber.builder()
                .accountNumber(number)
                .accountType(SAVINGS)
                .build();
        freeAccountNumbersRepository.save(freeAccountNumber);

        String result = freeAccountNumberService.getFreeAccountNumber(SAVINGS);
        List<FreeAccountNumber> list = freeAccountNumbersRepository.findAll();

        list.forEach(System.out::println);
        assertEquals(number, result);
        assertEquals(0, list.size());
    }

    @Test
    void testGetFreeAccountNumberWhenFreeAccountNumberNotExists() {
        List<FreeAccountNumber> list = freeAccountNumbersRepository.findAll();
        String result = freeAccountNumberService.getFreeAccountNumber(SAVINGS);

        assertEquals(0, list.size());
        assertEquals(number, result);
    }

    @Test
    void testGetQuantityFreeAccountNumbersByType() {
        int countSavingAccounts = 12;
        int countCreditAccounts = 15;
        int countDebitAccounts = 13;
        List<FreeAccountNumber> expected = new ArrayList<>();
        expected.addAll(createFreeAccountNumbers(SAVINGS, countSavingAccounts));
        expected.addAll(createFreeAccountNumbers(CREDIT, countCreditAccounts));
        expected.addAll(createFreeAccountNumbers(DEBIT, countDebitAccounts));

        freeAccountNumbersRepository.saveAll(expected);
        int savingResult = freeAccountNumberService.getQuantityFreeAccountNumbersByType(SAVINGS);
        int creditResult = freeAccountNumberService.getQuantityFreeAccountNumbersByType(CREDIT);
        int debitResult = freeAccountNumberService.getQuantityFreeAccountNumbersByType(DEBIT);

        assertEquals(countSavingAccounts, savingResult);
        assertEquals(countCreditAccounts, creditResult);
        assertEquals(countDebitAccounts, debitResult);
    }

    @Test
    void testGenerateAccountNumbers() {
        long oldNumbersSize = 10;
        List<FreeAccountNumber> numbers = createFreeAccountNumbers(SAVINGS, oldNumbersSize);
        freeAccountNumbersRepository.saveAll(numbers);
        AccountNumbersSequence accountNumbersSequence = findSequence(SAVINGS);
        accountNumbersSequence.setCurrentNumber(oldNumbersSize);
        accountNumbersSequenceRepository.save(accountNumbersSequence);

        List<FreeAccountNumber> generatedNumbers = freeAccountNumberService
                .generateAccountNumbers(SAVINGS, batchSize);
        List<FreeAccountNumber> numbersAtRepo = freeAccountNumbersRepository.findAll();
        AccountNumbersSequence sequence = findSequence(SAVINGS);

        numbersAtRepo.forEach(num -> assertEquals(num.getAccountNumber().length(), accountNumberLength));
        assertEquals(batchSize, generatedNumbers.size());
        assertEquals(batchSize + oldNumbersSize, sequence.getCurrentNumber());
        assertEquals(batchSize + oldNumbersSize, numbersAtRepo.size());
    }

    @Test
    void testGenerateAccountNumbersWhenSequenceDoesNotExist() {
        AccountNumbersSequence savings = findSequence(SAVINGS);
        accountNumbersSequenceRepository.delete(savings);
        assertFalse(accountNumbersSequenceRepository.existsById(SAVINGS.getCode()));
        List<FreeAccountNumber> generatedNumbers = freeAccountNumberService
                .generateAccountNumbers(SAVINGS, batchSize);
        List<FreeAccountNumber> numbersAtRepo = freeAccountNumbersRepository.findAll();

        assertTrue(accountNumbersSequenceRepository.existsById(SAVINGS.getCode()));
        assertEquals(batchSize, generatedNumbers.size());
        assertEquals(batchSize, numbersAtRepo.size());
    }

    private AccountNumbersSequence findSequence(AccountType type) {
        return accountNumbersSequenceRepository
                .findByAccountType(type.getCode())
                .orElseThrow();
    }

    private List<FreeAccountNumber> createFreeAccountNumbers(AccountType accountType, long count) {
        return LongStream.range(0, count)
                .mapToObj(n -> FreeAccountNumber.builder()
                        .accountType(accountType)
                        .accountNumber(freeAccountNumberMapper
                                .toFreeAccountNumber(accountType, n, accountNumberLength - typeCodeLength)
                                .getAccountNumber())
                        .build())
                .toList();
    }
}