package faang.school.accountservice.controller.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.account.BalanceDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.Balance;
import faang.school.accountservice.enums.account.AccountStatus;
import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.enums.currency.Currency;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.repository.account.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@ExtendWith(MockitoExtension.class)
class BalanceControllerIT {
    @Container
    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("testDb")
                    .withUsername("testUser")
                    .withPassword("testPass");

    private static final String TEST_LIQUIBASE_PATH =
            "classpath:db/changelog/testchangeset/balancecontroller/db.balance-test.yaml";

    @DynamicPropertySource
    private static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.liquibase.change-log", () -> TEST_LIQUIBASE_PATH);
    }

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BalanceController balanceController;

    private MockMvc mockMvc;


    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(balanceController).build();
    }

    @Test
    public void testGetBalanceByAccount() throws Exception {
        Account account = getAccount();
        Balance balance = getBalance(account);
        balanceRepository.save(balance);

        mockMvc.perform(get("/accounts/{accountId}/balances", account.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(balance.getId()))
                .andExpect(jsonPath("$.authorisationBalance").value(balance.getAuthorisationBalance()))
                .andExpect(jsonPath("$.actualBalance").value(balance.getActualBalance()));
    }

    @Test
    public void testCreateBalanceForAccount() throws Exception {
        Account account = getAccount();
        accountRepository.save(account);

        mockMvc.perform(post("/accounts/{accountId}/balances", account.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorisationBalance").value(BigDecimal.ZERO))
                .andExpect(jsonPath("$.actualBalance").value(BigDecimal.ZERO));
    }

    @Test
    public void testUpdateBalanceForAccount() throws Exception {
        BalanceDto balanceDto = getBalanceDto();
        Account account = getAccount();
        Balance balance = getBalance(account);
        balanceRepository.save(balance);


        mockMvc.perform(patch("/accounts/balances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(balanceDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(balanceDto.getId()))
                .andExpect(jsonPath("$.authorisationBalance").value(balanceDto.getAuthorisationBalance()))
                .andExpect(jsonPath("$.actualBalance").value(balanceDto.getActualBalance()));
    }

    private Balance getBalance(Account account) {
        return new Balance().setAccount(account);
    }

    private BalanceDto getBalanceDto() {
        return BalanceDto.builder()
                .id(1L)
                .authorisationBalance(BigDecimal.valueOf(1))
                .actualBalance(BigDecimal.valueOf(2))
                .build();
    }

    private Account getAccount() {
        return Account.builder()
                .id(1L)
                .paymentNumber("123456789012")
                .type(AccountType.INVESTMENT_ACCOUNT)
                .currency(Currency.USD)
                .status(AccountStatus.ACTIVE)
                .build();
    }
}