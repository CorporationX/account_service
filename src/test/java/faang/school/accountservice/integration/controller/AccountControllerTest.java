package faang.school.accountservice.integration.controller;

import com.redis.testcontainers.RedisContainer;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.controller.AccountController;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.ConflictException;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountControllerTest {
    @Autowired
    private AccountController accountController;

    @MockBean
    private UserContext userContext;

    @Autowired
    private AccountRepository accountRepository;

    @Container
    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:13.3");


    @Container
    public static final RedisContainer REDIS_CONTAINER = new RedisContainer(DockerImageName.parse("redis:6.2.6"));


    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        POSTGRESQL_CONTAINER.start();
        REDIS_CONTAINER.start();
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);


        registry.add("spring.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.redis.port", REDIS_CONTAINER::getFirstMappedPort);

        while (!POSTGRESQL_CONTAINER.isRunning()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @BeforeEach
    public void setup() {
        when(userContext.getUserId()).thenReturn(2L);
    }

    @AfterEach
    public void tearDown() {
        accountRepository.deleteAll();
    }

    @Test
    public void getAccount() {
        Account account = new Account();
        account.setAccountType(AccountType.PERSONAL);
        account.setStatus(AccountStatus.ACTIVE);
        account.setNumber("1234567890123");
        account.setVersion(1);
        Account createdAccount = accountRepository.save(account);

        ResponseEntity<AccountDto> result = accountController.getAccount(createdAccount.getId());

        Assertions.assertNotNull(result);
        assertEquals(createdAccount.getId(), result.getBody().getId());
        assertEquals("PERSONAL", result.getBody().getAccountType());
        assertEquals(AccountStatus.ACTIVE, result.getBody().getStatus());
    }

    @Test
    public void getAccountNotFound() {
        AccountNotFoundException accountNotFoundException = assertThrows(AccountNotFoundException.class, () -> {
             accountController.getAccount(1L);
        });

        assertEquals("Account not found", accountNotFoundException.getMessage());
    }


    @Test
    public void createAccount() {
        AccountDto accountDto = AccountDto.builder()
                .id(2L)
                .number("1234567890124")
                .accountType("PERSONAL")
                .currency(Currency.USD)
                .status(AccountStatus.ACTIVE)
                .version(1)
                .build();

        ResponseEntity<AccountDto> result = accountController.openAccount(accountDto);

        Account account = accountRepository.findById(Objects.requireNonNull(result.getBody()).getId()).orElse(null);


        assertNotNull(result);
        assertNotNull(account);
        assertEquals("PERSONAL", result.getBody().getAccountType());
        assertEquals(AccountStatus.ACTIVE, result.getBody().getStatus());
        assertEquals("1234567890124", result.getBody().getNumber());
    }

    @Test
    public void createAccountWithExistingNumber() {
        Account account = new Account();
        account.setId(1L);
        account.setAccountType(AccountType.PERSONAL);
        account.setStatus(AccountStatus.ACTIVE);
        account.setNumber("1234567890123");
        account.setVersion(1);
        accountRepository.save(account);

        AccountDto accountDto = AccountDto.builder()
                .id(2L)
                .number("1234567890123")
                .accountType("PERSONAL")
                .currency(Currency.USD)
                .status(AccountStatus.ACTIVE)
                .version(1)
                .build();

        ConflictException conflictException = assertThrows(ConflictException.class, () -> {
            accountController.openAccount(accountDto);
        });

        assertEquals("Account with number 1234567890123 already exists", conflictException.getMessage());
    }

    @Test
    public void blockAccount() {
        Account account = new Account();
        account.setId(1L);
        account.setAccountType(AccountType.PERSONAL);
        account.setStatus(AccountStatus.ACTIVE);
        account.setNumber("1234567890123");
        account.setVersion(1);
        accountRepository.save(account);

        ResponseEntity<AccountDto> result = accountController.blockAccount(1L);

        assertNotNull(result);
        assertEquals(1L, result.getBody().getId());
        assertEquals(AccountStatus.BLOCKED, result.getBody().getStatus());
    }

    @Test
    public void blockAccountNotFound() {

        EntityNotFoundException accountNotFoundException = assertThrows(EntityNotFoundException.class, () -> {
            accountController.blockAccount(1L);
        });

        assertEquals("Account not found", accountNotFoundException.getMessage());
    }

    @Test
    public void closeAccount() {
        Account account = new Account();
        account.setAccountType(AccountType.PERSONAL);
        account.setStatus(AccountStatus.ACTIVE);
        account.setNumber("1234567890123");
        account.setVersion(1);
        Account closedAccount = accountRepository.save(account);

        ResponseEntity<AccountDto> result = accountController.closeAccount(closedAccount.getId());

        assertNotNull(result);
        assertEquals(AccountStatus.CLOSED, result.getBody().getStatus());
    }

    @Test
    public void closeAccountNotFound() {
        EntityNotFoundException accountNotFoundException = assertThrows(EntityNotFoundException.class, () -> {
            accountController.closeAccount(1L);
        });

        assertEquals("Account not found", accountNotFoundException.getMessage());
    }

    @Test
    public void getAccountByNumber() {
        Account account = new Account();
        account.setAccountType(AccountType.PERSONAL);
        account.setStatus(AccountStatus.ACTIVE);
        account.setNumber("1234567890123");
        account.setVersion(1);
        Account getAccount = accountRepository.save(account);

        AccountDto result = accountController.getAccountByNumber("1234567890123");

        assertNotNull(result);
        assertEquals(getAccount.getId(), result.getId());
        assertEquals(AccountStatus.ACTIVE, result.getStatus());
        assertEquals("PERSONAL", result.getAccountType());
    }
}