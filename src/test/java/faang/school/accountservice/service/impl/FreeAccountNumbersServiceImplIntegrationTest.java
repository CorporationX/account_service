package faang.school.accountservice.service.impl;

import faang.school.accountservice.model.entity.FreeAccountNumber;
import faang.school.accountservice.model.entity.FreeAccountNumberId;
import faang.school.accountservice.model.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@Transactional
public class FreeAccountNumbersServiceImplIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:13.6");

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    @Autowired
    private FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Autowired
    private FreeAccountNumbersServiceImpl freeAccountNumbersService;

    private Map<AccountType, FreeAccountNumber> freeAccountNumberMap;

    @BeforeEach
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void setUp() {
        assertTrue(postgreSQLContainer.isRunning());

        accountNumbersSequenceRepository.deleteAll();
        freeAccountNumbersRepository.deleteAll();

        freeAccountNumberMap = Map.of(
                AccountType.INDIVIDUAL, new FreeAccountNumber(new FreeAccountNumberId(AccountType.INDIVIDUAL, "1234567890")),
                AccountType.CORPORATE, new FreeAccountNumber(new FreeAccountNumberId(AccountType.CORPORATE, "1234567890")),
                AccountType.SAVINGS, new FreeAccountNumber(new FreeAccountNumberId(AccountType.SAVINGS, "1234567890")),
                AccountType.INVESTMENT, new FreeAccountNumber(new FreeAccountNumberId(AccountType.INVESTMENT, "1234567890")),
                AccountType.RETIREMENT, new FreeAccountNumber(new FreeAccountNumberId(AccountType.RETIREMENT, "1234567890")),
                AccountType.STUDENT, new FreeAccountNumber(new FreeAccountNumberId(AccountType.STUDENT, "1234567890")),
                AccountType.BUSINESS, new FreeAccountNumber(new FreeAccountNumberId(AccountType.BUSINESS, "1234567890")),
                AccountType.PREPAID, new FreeAccountNumber(new FreeAccountNumberId(AccountType.PREPAID, "1234567890"))
        );
        freeAccountNumbersRepository.saveAll(freeAccountNumberMap.values());
    }

    @ParameterizedTest
    @EnumSource(AccountType.class)
    void testGetFreeAccountNumber_shouldReturnExistingOrAddNew(AccountType accountType) {
        FreeAccountNumber expectedNumber = freeAccountNumberMap.get(accountType);

        freeAccountNumbersService.getFreeAccountNumber(accountType, accountNumber -> {
            assertEquals(expectedNumber, accountNumber);
        });

        entityManager.flush();
        entityManager.clear();

        Optional<FreeAccountNumber> deletedNumber = freeAccountNumbersRepository.findById(expectedNumber.getId());

        assertFalse(deletedNumber.isPresent(), "Reserved account number should be deleted from the database");

        freeAccountNumbersRepository.deleteAll();

        freeAccountNumbersService.getFreeAccountNumber(accountType, accountNumber -> {
            assertNotEquals(expectedNumber, accountNumber, "A new account number should be generated");
            assertNotNull(accountNumber.getId().getNumber(), "Generated account number should not be null");

            Optional<FreeAccountNumber> newNumber = freeAccountNumbersRepository.findById(accountNumber.getId());
            assertFalse(newNumber.isPresent(), "New account number should not be in the database after retrieval (if not saved)");
        });
    }

    @ParameterizedTest
    @EnumSource(AccountType.class)
    void testAddFreeAccountNumber_shouldGenerateUniqueAccountNumber(AccountType accountType) {
        FreeAccountNumber generatedAccountNumber = freeAccountNumbersService.addFreeAccountNumber(accountType, true);

        assertEquals(accountType, generatedAccountNumber.getId().getType());
        assertEquals(accountType.getLength(), generatedAccountNumber.getId().getNumber().length());
        assertTrue(generatedAccountNumber.getId().getNumber().startsWith(String.format("%04d", accountType.getType())));
        assertTrue(generatedAccountNumber.getId().getNumber().endsWith("1"));
    }
}
