package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountNumberType;
import faang.school.accountservice.model.AccountNumberSequence;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.repository.AccountNumberSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Testcontainers
public class FreeAccountNumberServiceIT {

    @Container
    static PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private FreeAccountNumberService freeAccountNumberService;
    @Autowired
    private FreeAccountNumberRepository freeAccountNumberRepository;
    @Autowired
    private AccountNumberSequenceRepository accountNumberSequenceRepository;

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
    }

    @BeforeEach
    void setUp() {
        freeAccountNumberRepository.deleteAll();
        accountNumberSequenceRepository.deleteAll();

        AccountNumberSequence debitAccountNumberSequence = AccountNumberSequence.builder()
                .type(AccountNumberType.DEBIT)
                .build();
        AccountNumberSequence savingsAccountNumberSequence = AccountNumberSequence.builder()
                .type(AccountNumberType.SAVINGS)
                .build();
        accountNumberSequenceRepository.save(debitAccountNumberSequence);
        accountNumberSequenceRepository.save(savingsAccountNumberSequence);
    }

    @Test
    public void testCreateAccountNumber() {
        // arrange
        AccountNumberType accountNumberType = AccountNumberType.DEBIT;
        long expectedAccountNumberLength = 16;
        long expectedCurrentCount = 3;

        // act
        freeAccountNumberService.createAccountNumber(accountNumberType);
        freeAccountNumberService.createAccountNumber(accountNumberType);
        freeAccountNumberService.createAccountNumber(accountNumberType);

        // assert
        List<FreeAccountNumber> freeAccountNumbers = freeAccountNumberRepository.findAll();
        FreeAccountNumber freeAccountNumber = freeAccountNumbers.stream().findFirst().get();
        assertFalse(freeAccountNumbers.isEmpty());
        assertEquals(expectedAccountNumberLength, freeAccountNumber.getAccountNumber().length());
        assertEquals(AccountNumberType.DEBIT, freeAccountNumber.getType());

        AccountNumberSequence accountNumberSequence = accountNumberSequenceRepository.findByType(accountNumberType);
        assertEquals(expectedCurrentCount, accountNumberSequence.getCurrent());
    }

    @Test
    public void testProcessAccountNumber() {
        // arrange
        AccountNumberType accountNumberType = AccountNumberType.DEBIT;
        freeAccountNumberService.createAccountNumber(accountNumberType);
        freeAccountNumberService.createAccountNumber(accountNumberType);
        freeAccountNumberService.createAccountNumber(accountNumberType);
        Consumer<FreeAccountNumber> consumer = freeAccountNumber -> System.out.println("some action");
        long expectedAmountOfFreeAccountNumbers = 2;

        // act
        freeAccountNumberService.processFreeAccountNumber(accountNumberType, consumer);

        // assert
        List<FreeAccountNumber> freeAccountNumbers = freeAccountNumberRepository.findAll();
        assertEquals(expectedAmountOfFreeAccountNumbers, freeAccountNumbers.size());
    }

    @Test
    public void testProcessAccountNumberCreatesFreeAccountIfAbsent() {
        // arrange
        AccountNumberType accountNumberType = AccountNumberType.DEBIT;
        long expectedAmountOfFreeAccountNumbers = 0;

        // act and assert
        Consumer<FreeAccountNumber> consumer = Assertions::assertNotNull;
        freeAccountNumberService.processFreeAccountNumber(accountNumberType, consumer);
        List<FreeAccountNumber> freeAccountNumbers = freeAccountNumberRepository.findAll();
        assertEquals(expectedAmountOfFreeAccountNumbers, freeAccountNumbers.size());
    }
}
