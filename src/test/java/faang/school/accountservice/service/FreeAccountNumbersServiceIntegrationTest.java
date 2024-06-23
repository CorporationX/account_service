package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.jpa.AccountNumbersSequenceJpaRepository;
import faang.school.accountservice.repository.jpa.FreeAccountNumbersJpaRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class FreeAccountNumbersServiceIntegrationTest {
    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("testDB")
                    .withUsername("testUser")
                    .withPassword("testPassword");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private FreeAccountNumbersService freeAccountNumbersService;
    @Autowired
    private FreeAccountNumbersJpaRepository freeAccountNumbersJpaRepository;
    @Autowired
    private AccountNumbersSequenceJpaRepository accountNumbersSequenceJpaRepository;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() {
        freeAccountNumbersJpaRepository.deleteAll();
        accountNumbersSequenceJpaRepository.deleteAll();
    }

    @ParameterizedTest
    @EnumSource(AccountType.class)
    void createNewAccountNumberTest(AccountType type) {
        var sequence = AccountNumbersSequence
                .builder()
                .sequence(1L)
                .type(type)
                .build();
        var number = String.format("%d%016d",
                sequence.getType().getCode(),
                sequence.getSequence());

        freeAccountNumbersService.createNewAccountNumber(type);

        List<FreeAccountNumber> freeAccountNumberList = freeAccountNumbersJpaRepository.findAll();

        assertEquals(1, freeAccountNumberList.size());
        assertEquals(type, freeAccountNumberList.get(0).getType());
        assertEquals(number, freeAccountNumberList.get(0).getNumber());
    }

    @ParameterizedTest
    @EnumSource(AccountType.class)
    void consumeFreeNumberTest(AccountType type) {
        var sequence = AccountNumbersSequence
                .builder()
                .sequence(1L)
                .type(type)
                .build();
        var number = String.format("%d%016d",
                sequence.getType().getCode(),
                sequence.getSequence());

        String[] result = new String[1];
        Consumer<String> numberConsumer = string -> result[0] = string;


        freeAccountNumbersService.consumeFreeNumber(type, numberConsumer);


        assertEquals(number, result[0]);

        List<FreeAccountNumber> freeAccountNumberList = freeAccountNumbersJpaRepository.findAll();
        assertEquals(0, freeAccountNumberList.size());
    }
}