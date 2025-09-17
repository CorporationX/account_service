package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.EntityAlreadyBlockedException;
import faang.school.accountservice.exception.EntityAlreadyClosedException;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.validator.AccountValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
@Testcontainers
@ActiveProfiles("test")
@Sql(scripts = "/db/script/account_insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/db/script/account_cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AccountServiceIntTest {
    @Autowired
    private AccountService service;
    @Autowired
    private AccountRepository repository;
    @Autowired
    private AccountMapper mapper;
    @MockBean
    private AccountValidator validator;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:13.3");
    private static final long USER_ID = 1;
    private static final long PROJECT_ID = 1;
    private static final UUID USER_ACCOUNT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID BLOCKED_ACCOUNT_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID CLOSED_ACCOUNT_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID PROJECT_ACCOUNT_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID UNKNOWN_ACCOUNT_ID = UUID.fromString("10101010-1010-0101-1010-010101010101");
    private static final String ACCOUNT_NUMBER = "12345678912345";
    private static final String UNKNOWN_ACCOUNT_NUMBER = "12121212121212";

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

    @ParameterizedTest
    @CsvSource(value = {
            "1, null",
            "null, 1"
    }, nullValues = {"null"})
    @DisplayName("Успешное создание счета")
    void positive_shouldCreateAccount(Long userId, Long projectId) {
        CreateAccountDto createAccountDto = createCreateAccountDto(userId, projectId);

        AccountDto accountDto = service.create(createAccountDto);

        assertTrue(repository.findById(accountDto.id()).isPresent());
    }

    @Test
    @DisplayName("Успешное получение счета по id")
    void positive_shouldFindAccountById() {
        AccountDto actual = service.findById(USER_ACCOUNT_ID);

        assertNotNull(actual);
        assertEquals(USER_ACCOUNT_ID, actual.id());
    }

    @Test
    @DisplayName("Успешное получение счета по номеру")
    void positive_shouldFindAccountByNumber() {
        AccountDto actual = service.findByNumber(ACCOUNT_NUMBER);

        assertNotNull(actual);
        assertEquals(ACCOUNT_NUMBER, actual.number());
    }

    @Test
    @DisplayName("Успешное получение списка счетов юзера")
    void positive_shouldFindAccountsByUserId() {
        List<AccountDto> actual = service.findByUserId(USER_ID);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(USER_ID, actual.get(0).userId());
    }

    @Test
    @DisplayName("Успешное получение списка счетов проекта")
    void positive_shouldFindAccountsByProjectId() {
        List<AccountDto> actual = service.findByProjectId(PROJECT_ID);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(PROJECT_ID, actual.get(0).projectId());
    }

    @Test
    @DisplayName("Успешная блокировка счета")
    void positive_shouldBlockAccount() {
        service.block(USER_ACCOUNT_ID);

        Account actual = repository.findById(USER_ACCOUNT_ID).get();
        assertEquals(AccountStatus.FROZEN, actual.getStatus());
    }

    @Test
    @DisplayName("Успешное закрытие счета")
    void positive_shouldCloseAccount() {
        service.close(PROJECT_ACCOUNT_ID);

        Account actual = repository.findById(PROJECT_ACCOUNT_ID).get();
        assertEquals(AccountStatus.CLOSED, actual.getStatus());
    }

    @Test
    @DisplayName("Ошибка получения счета по id - счет не найден")
    void negative_whenAccountNotFoundById_throwsError() {
        String expectedMessage = "Account not found by id=" + UNKNOWN_ACCOUNT_ID;

        String actualMessage = assertThrows(EntityNotFoundException.class,
                                            () -> service.findById(UNKNOWN_ACCOUNT_ID)).getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Ошибка получения счета по номеру - счет не найден")
    void negative_whenAccountNotFoundByNumber_throwsError() {
        String expectedMessage = "Account not found by number=" + UNKNOWN_ACCOUNT_NUMBER;

        String actualMessage = assertThrows(EntityNotFoundException.class,
                                            () -> service.findByNumber(UNKNOWN_ACCOUNT_NUMBER)).getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Ошибка блокировки счета - счет не найден")
    void negative_whenAccountNotFound_notBlockAndThrowsError() {
        String expectedMessage = "Account not found by id=" + UNKNOWN_ACCOUNT_ID;

        String actualMessage = assertThrows(EntityNotFoundException.class,
                                            () -> service.block(UNKNOWN_ACCOUNT_ID)).getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Ошибка блокировки счета - счет уже закрыт")
    void negative_whenAccountAlreadyClosed_notBlockAndThrowsError() {
        assertThrows(EntityAlreadyClosedException.class,
                     () -> service.block(CLOSED_ACCOUNT_ID));
    }

    @Test
    @DisplayName("Ошибка блокировки счета - счет уже заблокирован")
    void negative_whenAccountAlreadyBlocked_notBlockAndThrowsError() {
        assertThrows(EntityAlreadyBlockedException.class,
                     () -> service.block(BLOCKED_ACCOUNT_ID));
    }

    @Test
    @DisplayName("Ошибка закрытия счета - счет не найден")
    void negative_whenAccountNotFound_notCloseAndThrowsError() {
        String expectedMessage = "Account not found by id=" + UNKNOWN_ACCOUNT_ID;

        String actualMessage = assertThrows(EntityNotFoundException.class,
                                            () -> service.close(UNKNOWN_ACCOUNT_ID)).getMessage();

        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    @DisplayName("Ошибка закрытия счета - счет уже закрыт")
    void negative_whenAccountAlreadyClosed_notCloseAndThrowsError() {
        assertThrows(EntityAlreadyClosedException.class,
                     () -> service.close(CLOSED_ACCOUNT_ID));
    }

    // ---------------------------

    private CreateAccountDto createCreateAccountDto(Long userId, Long projectId) {
        return new CreateAccountDto(userId, projectId, AccountType.PERSONAL_CURRENT, Currency.RUB);
    }
}