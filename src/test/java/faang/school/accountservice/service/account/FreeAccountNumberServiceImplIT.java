package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.account.AccountSequence;
import faang.school.accountservice.entity.account.FreeAccountId;
import faang.school.accountservice.entity.account.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FreeAccountNumberServiceImplIT {

    @SuppressWarnings("resource")
    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @Autowired
    private FreeAccountNumberServiceImpl freeAccountNumberService;

    @Autowired
    private FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Autowired
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

    private static final AccountType TYPE = AccountType.PERSONAL;

    @BeforeEach
    public void setUp() {
        freeAccountNumbersRepository.deleteAll();
        accountNumbersSequenceRepository.deleteAll();

        AccountSequence seq = new AccountSequence();
        seq.setAccountType(TYPE);
        seq.setCounter(0L);
        accountNumbersSequenceRepository.save(seq);
    }

    @Test
    @DisplayName("Generates a batch: saves numbers and advances the counter")
    public void generatesBatchSavesNumbersAndIncrementsCounter() {
        int batch = 5;

        long beforeCounter = getSequenceCounterForType();
        long beforeFree = countFreeNumbersForType();

        freeAccountNumberService.generateAccountNumbers(TYPE, batch);

        long afterCounter = getSequenceCounterForType();
        long afterFree = countFreeNumbersForType();

        assertEquals(beforeCounter + batch, afterCounter);
        assertEquals(beforeFree + batch, afterFree);
    }

    @Test
    @DisplayName("Retrieves a free number and removes it")
    public void retrieveAccountNumber() {
        freeAccountNumbersRepository.save(new FreeAccountNumber(new FreeAccountId(TYPE, 1L)));
        freeAccountNumbersRepository.save(new FreeAccountNumber(new FreeAccountId(TYPE, 2L)));

        freeAccountNumberService.retrieveAccountNumber(TYPE, freeAccountNumber -> {
        });

        List<FreeAccountNumber> allFreeNumbers = freeAccountNumbersRepository.findAll();
        List<FreeAccountNumber> freeNumbersOfThisType = new ArrayList<>();
        for (FreeAccountNumber number : allFreeNumbers) {
            if (number.getId().getType() == TYPE) {
                freeNumbersOfThisType.add(number);
            }
        }

        assertEquals(1, freeNumbersOfThisType.size());

        FreeAccountNumber freeNumberLeft = freeNumbersOfThisType.get(0);
        assertEquals(2L, freeNumberLeft.getId().getAccountNumber());
    }


    @Test
    @DisplayName("Generates when none are available and increments the counter")
    public void generatesWhenEmptyIncrementsCounter() {
        assertEquals(0, countFreeNumbersForType());
        long counterBefore = getSequenceCounterForType();

        freeAccountNumberService.retrieveAccountNumber(TYPE, n -> {
        });

        assertEquals(0, countFreeNumbersForType());
        assertEquals(counterBefore + 1, getSequenceCounterForType());
    }

    private long countFreeNumbersForType() {
        return freeAccountNumbersRepository.findAll().stream()
                .filter(e -> e.getId().getType() == TYPE)
                .count();
    }

    private long getSequenceCounterForType() {
        return accountNumbersSequenceRepository.findAll().stream()
                .filter(s -> s.getAccountType() == TYPE)
                .findFirst()
                .map(AccountSequence::getCounter)
                .orElseThrow(() -> new IllegalStateException("sequence row missing for " + TYPE));
    }
}
