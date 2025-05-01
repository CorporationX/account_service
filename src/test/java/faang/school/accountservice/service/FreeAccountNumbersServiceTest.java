package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.exception.DuplicateAccountNumberException;
import faang.school.accountservice.exception.SequenceNotInitializedException;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.service.account.FreeAccountNumbersServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(FreeAccountNumbersServiceImpl.class)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EntityScan("faang.school.accountservice.entity")
@EnableJpaRepositories("faang.school.accountservice.repository")
public class FreeAccountNumbersServiceTest {
    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.3")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private FreeAccountNumbersServiceImpl numbersService;

    @Autowired
    private FreeAccountNumbersRepository numbersRepo;

    @Autowired
    private AccountNumbersSequenceRepository sequenceRepo;

    private final String TYPE = "SAVINGS";
    private final String PREFIX = "ACC";
    private final int TOTAL_LENGTH = 10;

    @BeforeEach
    void setUp() {
        numbersRepo.deleteAll();
        sequenceRepo.deleteAll();
    }

    @Test
    public void testAddFreeAccountNumber_Successfully() {
        String accountNumber = "123";

        numbersService.addFreeAccountNumber(TYPE, accountNumber);

        Optional<FreeAccountNumber> saved = numbersRepo.findByKeyAccountNumber(accountNumber);
        assertTrue(saved.isPresent(), "Free account number should be saved in repository");
        assertEquals(TYPE, saved.get().getKey().getAccountType());
    }

    @Test
    public void testAddFreeAccountNumber_DuplicateThrowsException() {
        String accountNumber = "321";
        numbersService.addFreeAccountNumber(TYPE, accountNumber);

        assertThrows(DuplicateAccountNumberException.class, () ->
                numbersService.addFreeAccountNumber(TYPE, accountNumber), "Adding duplicate account number must throw DuplicateAccountNumberException");
    }

    @Test
    public void testWithNewAccountNumber_FreeNumberExist() {
        String freeNumber = "001";

        FreeAccountNumber.Key key = new FreeAccountNumber.Key(TYPE, freeNumber);
        FreeAccountNumber entity = new FreeAccountNumber(key, Instant.now());
        numbersRepo.save(entity);

        String result = numbersService.withNewAccountNumber(TYPE, number -> number, PREFIX, TOTAL_LENGTH);
        assertEquals(freeNumber, result, "Should return the existing free number");

        Optional<FreeAccountNumber> deleted = numbersRepo.findByKeyAccountNumber(freeNumber);
        assertFalse(deleted.isPresent(), "Free number should be removed after use");
    }

    @Test
    public void testWithNewAccountNumber_GenerateNewAccountNumber() {
        AccountNumbersSequence sequence = new AccountNumbersSequence();
        sequence.setAccountType(TYPE);
        sequence.setCurrentValue(5L);
        sequenceRepo.save(sequence);

        String expectedNumber = PREFIX + String.format("%0" + (TOTAL_LENGTH - PREFIX.length()) + "d", 6);
        String result = numbersService.withNewAccountNumber(TYPE, number -> number, PREFIX, TOTAL_LENGTH);
        assertEquals(expectedNumber, result, "Should generate a new account number based on sequence");

        AccountNumbersSequence updated = sequenceRepo.findById(TYPE).orElseThrow();
        assertEquals(6L, updated.getCurrentValue(), "Sequence currentValue should be incremented");
    }

    @Test
    public void testWithNewAccountNumber_SequenceInitializationError() {
        assertThrows(SequenceNotInitializedException.class, () ->
                numbersService.withNewAccountNumber(TYPE, number -> number, PREFIX, TOTAL_LENGTH), "Missing sequence should throw SequenceNotInitializedException");
    }
}
