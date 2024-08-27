package faang.school.accountservice;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.model.AccountNumberSequence;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.model.FreeAccountNumberId;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.service.FreeAccountNumbersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Testcontainers
@SpringBootTest

public class FreeAccountNumbersServiceTest {
    @Container
    public static final PostgreSQLContainer postgresContainer =
            new PostgreSQLContainer<>("postgres:13.3")
                    .withInitScript("init.sql");
    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        postgresContainer.start();
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private FreeAccountNumbersRepository freeAccountNumbersRepository;
    @Autowired
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    @Autowired
    private FreeAccountNumbersService freeAccountNumbersService;

    private List<AccountNumberSequence> accountNumberSequenceList;
    private Map<AccountType, FreeAccountNumber> freeAccountNumberMap;

    @BeforeEach
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void init() {
        assertTrue(postgresContainer.isRunning());

        accountNumbersSequenceRepository.deleteAll();
        accountNumberSequenceList = List.of(
                AccountNumberSequence.builder().accountType(AccountType.RETIREMENT).count(999L).build(),
                AccountNumberSequence.builder().accountType(AccountType.INDIVIDUAL).count(100L).build(),
                AccountNumberSequence.builder().accountType(AccountType.INVESTMENT).count(1_000L).build(),
                AccountNumberSequence.builder().accountType(AccountType.CORPORATE).count(5_000L).build()
        );

        accountNumbersSequenceRepository.saveAll(accountNumberSequenceList);

        freeAccountNumbersRepository.deleteAll();
        freeAccountNumberMap = Map.of(
                AccountType.RETIREMENT, new FreeAccountNumber(new FreeAccountNumberId(AccountType.RETIREMENT, BigInteger.valueOf(12345))),
                AccountType.INVESTMENT, new FreeAccountNumber(new FreeAccountNumberId(AccountType.INVESTMENT, BigInteger.valueOf(54321))),
                AccountType.INDIVIDUAL, new FreeAccountNumber(new FreeAccountNumberId(AccountType.INDIVIDUAL, BigInteger.valueOf(9999))),
                AccountType.CORPORATE, new FreeAccountNumber(new FreeAccountNumberId(AccountType.CORPORATE, BigInteger.valueOf(11111)))
        );
        freeAccountNumbersRepository.saveAll(freeAccountNumberMap.values());
    }

    @ParameterizedTest
    @EnumSource(AccountType.class)
    void getUniqueAccountNumberByType(AccountType accountType) {
        FreeAccountNumber accountNumber = freeAccountNumberMap.get(accountType);

        freeAccountNumbersService.getUniqueAccountNumberByType(
                freeAccountNumber -> assertEquals(accountNumber, freeAccountNumber),
                accountType);
    }

    @ParameterizedTest
    @EnumSource(AccountType.class)
    void generateNewAccountNumberByType(AccountType accountType) {
        FreeAccountNumber generatedNumber = freeAccountNumbersService.generateNewAccountNumberByType(accountType);

        assertEquals(accountType, generatedNumber.getId().getAccountType());
    }
}
