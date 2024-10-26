package faang.school.accountservice.integration;

import faang.school.accountservice.config.TestContainersConfig;
import faang.school.accountservice.enums.account.AccountType;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static faang.school.accountservice.enums.account.AccountType.CREDIT;
import static faang.school.accountservice.enums.account.AccountType.DEBIT;
import static faang.school.accountservice.enums.account.AccountType.SAVINGS;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@ContextConfiguration(classes = TestContainersConfig.class)
@EnableRetry
class FreeAccountNumberServiceIntegrationTest extends TestContainersConfig {
    @Autowired
    private FreeAccountNumberService freeAccountNumberService;

    @Autowired
    private FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Autowired
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    private final String number = "5536000000000000";

    @AfterEach
    public void cleanUp() {
        freeAccountNumbersRepository.deleteAll();
        List<AccountNumbersSequence> sequences = accountNumbersSequenceRepository.findAll();
        sequences.stream()
                .peek(sequence -> sequence.setCurrentNumber(0L))
                .forEach(accountNumbersSequenceRepository::save);
    }

    @Test
    void testCreateNextNumber() {
        IntStream.rangeClosed(0, 9)
                .forEach(n -> freeAccountNumberService.createNextNumber(SAVINGS, true));
        String numberWithoutLast = number.substring(0, number.length() - 1);
        List<FreeAccountNumber> expected = IntStream
                .rangeClosed(0, 9)
                .mapToObj(num -> FreeAccountNumber.builder()
                        .accountNumber(numberWithoutLast + num)
                        .accountType(SAVINGS.getCode())
                        .build())
                .toList();
        List<FreeAccountNumber> result = freeAccountNumbersRepository.findAll();

        assertEquals(expected, result);
    }

    @Test
    @Transactional
    void testGetFreeAccountNumberFromWhenFreeAccountNumberExists() {
        FreeAccountNumber freeAccountNumber = FreeAccountNumber.builder()
                .accountNumber(number)
                .accountType(SAVINGS.getCode())
                .build();
        freeAccountNumbersRepository.save(freeAccountNumber);

        String result = freeAccountNumberService.getFreeAccountNumber(SAVINGS);
        List<FreeAccountNumber> list = freeAccountNumbersRepository.findAll();

        assertEquals(number, result);
        assertEquals(0, list.size());
    }

    @Test
    void testGetFreeAccountNumberWhenFreeAccountNumberNotExists() {
        String result = freeAccountNumberService.getFreeAccountNumber(SAVINGS);
        List<FreeAccountNumber> list = freeAccountNumbersRepository.findAll();

        assertEquals(number, result);
        assertEquals(0, list.size());
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

    private List<FreeAccountNumber> createFreeAccountNumbers(AccountType accountType, int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(n -> FreeAccountNumber.builder()
                        .accountType(accountType.getCode())
                        .accountNumber(accountType.getCode() + n)
                        .build())
                .toList();
    }
}