package faang.school.accountservice.service;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.exception.EntityAlreadyExistsException;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.validator.AccountValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
@Testcontainers
@ActiveProfiles("test")
@Sql(scripts = "/db/script/savings_insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/db/script/savings_cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class SavingsAccountServiceIntTest {
    @Autowired
    private SavingsAccountService service;
    @Autowired
    private SavingsAccountRepository repository;
    @MockBean
    private AccountValidator validator;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:13.3");
    private static final String TARIFF_TYPE = "BASE";
    private static final long TARIFF_ID = 1L;
    private static final long HAS_SAVINGS_USER_ID = 1;
    private static final long NOT_SAVINGS_USER_ID = 2;
    private static final long HAS_SAVINGS_PROJECT_ID = 1;
    private static final long NOT_SAVINGS_PROJECT_ID = 2;
    private static final UUID SAVINGS_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ACCOUNT_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID UNKNOWN_ACCOUNT_ID = UUID.fromString("10101010-1010-0101-1010-010101010101");

    @DynamicPropertySource
    static void propertySource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Успешное открытие накопит. счета")
    void positive_shouldCreateSavingsAccount() {
        SavingsAccountDto actual = service.create(ACCOUNT_ID, TARIFF_ID);

        assertTrue(repository.findById(actual.account().id()).isPresent());
        assertNotNull(actual.createdAt());
        assertNotNull(actual.currentRate());
        assertEquals(TARIFF_TYPE, actual.currentTariff());
    }

    @Test
    @DisplayName("Успешное получение накопит. счета по id")
    void positive_shouldFindSavingsAccountById() {
        SavingsAccountDto actual = service.findById(SAVINGS_ID);

        assertNotNull(actual);
        assertEquals(SAVINGS_ID, actual.account().id());
    }

    @Test
    @DisplayName("Успешное получение накопит. счета по юзеру")
    void positive_shouldFindSavingsAccountsByUserId() {
        SavingsAccountDto actual = service.findByUserId(HAS_SAVINGS_USER_ID);

        assertNotNull(actual);
        assertEquals(HAS_SAVINGS_USER_ID, actual.account().userId());
    }

    @Test
    @DisplayName("Успешное получение накопит. счета по проекту")
    void positive_shouldFindAccountsByProjectId() {
        SavingsAccountDto actual = service.findByProjectId(HAS_SAVINGS_PROJECT_ID);

        assertNotNull(actual);
        assertEquals(HAS_SAVINGS_PROJECT_ID, actual.account().projectId());
    }

    @Test
    @DisplayName("Ошибка открытия накопит. счета - накоп. счет уже есть")
    void negative_whenSavingsAccountAlreadyExists_throwsError() {

        assertThrows(EntityAlreadyExistsException.class,
                     () -> service.create(SAVINGS_ID, TARIFF_ID));
    }

    @Test
    @DisplayName("Ошибка открытия накопит. счета - базовый счет не найден")
    void negative_whenCreateSavingsAccountNotFound_throwsError() {
        String expectedMessage = "Account not found by id = " + UNKNOWN_ACCOUNT_ID;

        String actualMessage = assertThrows(EntityNotFoundException.class,
                                            () -> service.create(UNKNOWN_ACCOUNT_ID, TARIFF_ID)).getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Ошибка получения счета по id - счет не найден")
    void negative_whenSavingsNotFoundById_throwsError() {
        String expectedMessage = "Savings account not found by id = " + UNKNOWN_ACCOUNT_ID;

        String actualMessage = assertThrows(EntityNotFoundException.class,
                                            () -> service.findById(UNKNOWN_ACCOUNT_ID)).getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Ошибка получения счета по userId - счет не найден")
    void negative_whenSavingsNotFoundByUserId_throwsError() {
        String expectedMessage = "Savings account not found by userId = " + NOT_SAVINGS_USER_ID;

        String actualMessage = assertThrows(EntityNotFoundException.class,
                                            () -> service.findByUserId(NOT_SAVINGS_USER_ID)).getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Ошибка получения счета по projectId - счет не найден")
    void negative_whenSavingsNotFoundByProjectId_throwsError() {
        String expectedMessage = "Savings account not found by projectId = " + NOT_SAVINGS_PROJECT_ID;

        String actualMessage = assertThrows(EntityNotFoundException.class,
                                            () -> service.findByProjectId(NOT_SAVINGS_PROJECT_ID)).getMessage();

        assertEquals(expectedMessage, actualMessage);
    }
}