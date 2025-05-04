package faang.school.accountservice.controller;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.entity.tariff.TariffHistory;
import faang.school.accountservice.entity.tariff.TariffRate;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class SavingsAccountIntegrationTest {

    private static final String USER_PARAMETER = "x-user-id";

    private final int userId = 100;
    private final String firstTariffName = "Gold";
    private final BigDecimal firstRate = new BigDecimal("5.00");
    private final BigDecimal secondRate = new BigDecimal("3.00");
    private final BigDecimal defaultBalance = new BigDecimal("0");
    private final LocalDateTime timeNow = LocalDateTime.now();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SavingsAccountRepository savingsAccountRepository;

    @Autowired
    private TariffRepository tariffRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @AfterEach
    void tearDown() {
        tariffRepository.deleteAll();
        savingsAccountRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void testNegativeOpenSavingsAccountWhenSavingsAccountExists() throws Exception {
        Account account = createAccount();
        Tariff tariff = tariffRepository.save(createTariff(firstTariffName, firstRate));
        SavingsAccount savingsAccount = createSavingsAccount(account, tariff);
        account.setSavingsAccount(savingsAccount);
        accountRepository.save(account);

        mockMvc.perform(post("/accounts/savings/{tariffId}", tariff.getId())
                        .header(USER_PARAMETER, userId))
                .andExpect(status().isConflict());
    }

    @Test
    void testPositiveOpenSavingsAccount() throws Exception {
        accountRepository.save(createAccount());
        Tariff tariff = tariffRepository.save(createTariff(firstTariffName, firstRate));

        mockMvc.perform(post("/accounts/savings/{tariffId}", tariff.getId())
                        .header(USER_PARAMETER, userId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber").exists())
                .andExpect(jsonPath("$.balance").value(defaultBalance))
                .andExpect(jsonPath("$.lastInterestAccrualAt").doesNotExist())
                .andExpect(jsonPath("$.activeTariff").value(tariff.getTypeName()))
                .andExpect(jsonPath("$.activeTariffRate").value(tariff.getRates().get(0).getRate()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void testNegativeGetSavingsAccountByIdWhenAccountNotFound() throws Exception {
        mockMvc.perform(get("/accounts/savings/{id}", userId)
                        .header(USER_PARAMETER, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPositiveGetSavingsAccountById() throws Exception {
        Account account = createAccount();
        Tariff tariff = tariffRepository.save(createTariff(firstTariffName, firstRate));
        SavingsAccount savingsAccount = createSavingsAccount(account, tariff);
        account.setSavingsAccount(savingsAccount);
        accountRepository.save(account);

        mockMvc.perform(get("/accounts/savings/{id}", savingsAccount.getAccountId())
                        .header(USER_PARAMETER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(savingsAccount.getAccountNumber()))
                .andExpect(jsonPath("$.balance").value(savingsAccount.getBalance()))
                .andExpect(jsonPath("$.lastInterestAccrualAt").value(savingsAccount.getLastInterestAccrualAt()))
                .andExpect(jsonPath("$.activeTariff").value(tariff.getTypeName()))
                .andExpect(jsonPath("$.activeTariffRate").value(tariff.getRates().get(0).getRate()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void testNegativeGetSavingsAccountByOwnerIdWhenOwnerAccountNotFound() throws Exception {
        mockMvc.perform(get("/accounts/savings/account/{ownerId}", userId)
                        .header(USER_PARAMETER, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPositiveGetSavingsAccountByOwnerId() throws Exception {
        Account account = createAccount();
        Tariff tariff = tariffRepository.save(createTariff(firstTariffName, firstRate));
        SavingsAccount savingsAccount = createSavingsAccount(account, tariff);
        account.setSavingsAccount(savingsAccount);
        accountRepository.save(account);

        mockMvc.perform(get("/accounts/savings/account/{ownerId}", userId)
                        .header(USER_PARAMETER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(savingsAccount.getAccountNumber()))
                .andExpect(jsonPath("$.balance").value(savingsAccount.getBalance()))
                .andExpect(jsonPath("$.lastInterestAccrualAt").value(savingsAccount.getLastInterestAccrualAt()))
                .andExpect(jsonPath("$.activeTariff").value(tariff.getTypeName()))
                .andExpect(jsonPath("$.activeTariffRate").value(tariff.getRates().get(0).getRate()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void testNegativeUpdateTariffOnSavingsAccountWhenTariffNotFound() throws Exception {
        mockMvc.perform(patch("/accounts/savings/{accountId}/tariff/{tariffId}",
                        userId, 1L)
                        .header(USER_PARAMETER, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPositiveUpdateTariffOnSavingsAccount() throws Exception {
        Account account = createAccount();
        Tariff firstTariff = tariffRepository.save(createTariff(firstTariffName, firstRate));
        Tariff secondTariff = tariffRepository.save(createTariff("Silver", secondRate));
        SavingsAccount savingsAccount = createSavingsAccount(account, firstTariff);
        account.setSavingsAccount(savingsAccount);
        accountRepository.save(account);

        mockMvc.perform(patch("/accounts/savings/{accountId}/tariff/{tariffId}",
                        savingsAccount.getAccountId(), secondTariff.getId())
                        .header(USER_PARAMETER, userId))
                .andExpect(status().isOk());
    }

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        POSTGRESQL_CONTAINER.start();

        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Tariff createTariff(String name, BigDecimal rate) {
        Tariff tariff = Tariff.builder()
                .typeName(name)
                .build();

        List<TariffRate> rates = new ArrayList<>();
        TariffRate tariffRate = createRate(rate, tariff);
        rates.add(tariffRate);
        tariff.setRates(rates);

        return tariff;
    }

    private TariffRate createRate(BigDecimal rate, Tariff tariff) {
        return TariffRate.builder()
                .rate(rate)
                .tariff(tariff)
                .build();
    }

    private Account createAccount() {
        return Account.builder()
                .number("123561341247137")
                .ownerType(OwnerType.USER)
                .ownerId(userId)
                .type(AccountType.DEBIT)
                .currency(Currency.USD)
                .status(AccountStatus.ACTIVE)
                .build();
    }

    private SavingsAccount createSavingsAccount(Account account, Tariff tariff) {
        SavingsAccount savingsAccount = SavingsAccount.builder()
                .account(account)
                .accountNumber("124341671324587")
                .balance(defaultBalance)
                .lastInterestAccrualAt(null)
                .createdAt(timeNow)
                .updatedAt(timeNow)
                .build();
        List<TariffHistory> historyList = new ArrayList<>();
        historyList.add(createTariffHistory(savingsAccount, tariff));
        savingsAccount.setTariffHistory(historyList);
        return savingsAccount;
    }

    private TariffHistory createTariffHistory(SavingsAccount savingsAccount, Tariff tariff) {
        return TariffHistory.builder()
                .tariff(tariff)
                .savingsAccount(savingsAccount)
                .build();
    }
}
