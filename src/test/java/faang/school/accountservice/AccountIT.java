package faang.school.accountservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.account.Account;
import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Bank;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class AccountIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    private long accountId;

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13:3");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void init() {
        accountRepository.deleteAll();
        AccountCreateDto accountCreateDto =
                new AccountCreateDto(6L, null, Bank.BELINVESTBANK, Currency.EUR, AccountType.BUSINESS);
        var res = accountService.openAccount(accountCreateDto);
        accountId = res.getId();
    }

    @Test
    public void openAccount_ShouldCreateAndReturn() throws Exception {
        AccountCreateDto accountCreateDto =
                new AccountCreateDto(10L, null, Bank.TINKOFF, Currency.USD, AccountType.CURRENT);
        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountCreateDto))
                        .header("x-user-id", 1L))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id", isA(Number.class)));
    }

    @Test
    public void openAccount_ShouldThrowWhenBothUserProjectIds() throws Exception {
        AccountCreateDto accountCreateDto =
                new AccountCreateDto(10L, 10L, Bank.TINKOFF, Currency.USD, AccountType.CURRENT);
        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountCreateDto))
                        .header("x-user-id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAccountByUserId_ShouldReturnUserAccount() throws Exception {
        mockMvc.perform(get("/account/user/6")
                        .header("x-user-id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].userId", is(6)));
    }

    @Test
    public void friezeAccount_ShouldFriezeAccountAndThrowWhenAlreadyFrozen() throws Exception {
        Account initAccount = accountRepository.findById(accountId).get();
        mockMvc.perform(put("/account/frieze/" + accountId)
                        .header("x-user-id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.userId", is(6)));

        Account account = accountRepository.findById(accountId).get();
        assertEquals(AccountStatus.FROZEN, account.getStatus());
        assertEquals(initAccount.getVersion(), account.getVersion() - 1);

        mockMvc.perform(put("/account/frieze/" + accountId)
                        .header("x-user-id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void closeAccount_ShouldCloseAccountAndThrowWhenAlreadyClosed() throws Exception {
        Account initAccount = accountRepository.findById(accountId).get();
        mockMvc.perform(put("/account/close/" + accountId)
                        .header("x-user-id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.userId", is(6)));

        Account account = accountRepository.findById(accountId).get();
        assertEquals(AccountStatus.CLOSED, account.getStatus());
        assertEquals(initAccount.getVersion(), account.getVersion() - 1);

        mockMvc.perform(put("/account/close/" + accountId)
                        .header("x-user-id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void closeAccount_ShouldThrowAfterMaxAttempts() {
        AccountRepository mockRepo = mock(AccountRepository.class);
        when(mockRepo.findById(any()))
                .thenThrow(ObjectOptimisticLockingFailureException.class);

        ReflectionTestUtils.setField(accountService, "accountRepository", mockRepo);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.closeAccount(accountId));
        assertEquals("Couldn't finish updating", exception.getMessage());
        verify(mockRepo, times(AccountService.RETRY_ATTEMPTS)).findById(accountId);
        ReflectionTestUtils.setField(accountService, "accountRepository", accountRepository);
    }
}
