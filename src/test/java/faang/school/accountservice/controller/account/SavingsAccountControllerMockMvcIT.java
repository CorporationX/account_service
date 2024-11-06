package faang.school.accountservice.controller.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.account.SavingsAccountCreatedDto;
import faang.school.accountservice.dto.account.SavingsAccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.SavingsAccount;
import faang.school.accountservice.entity.owner.Owner;
import faang.school.accountservice.entity.rate.Rate;
import faang.school.accountservice.entity.tariff.Tariff;
import faang.school.accountservice.entity.type.AccountType;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.repository.account.SavingsAccountRepository;
import faang.school.accountservice.repository.owner.OwnerRepository;
import faang.school.accountservice.repository.rate.RateRepository;
import faang.school.accountservice.repository.tariff.TariffRepository;
import faang.school.accountservice.repository.type.TypeRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class SavingsAccountControllerMockMvcIT {

    private static final Long ID = 1L;
    private static final String NAME = "name";
    private static final String TARIFF_NAME = "tariff";
    private static final String TARIFF_HISTORY = "[tariff]";
    private static final String RATE_HISTORY = "[1.0]";
    private static final Double INTEREST_RATE = 1.0;
    private static final String ACCOUNT_NUMBER = "123456789012";
    private static final BigDecimal BALANCE = BigDecimal.ZERO;
    private static final UUID NUMBER = UUID.randomUUID();
    private SavingsAccountCreatedDto createdDto;
    private SavingsAccount savingsAccount;
    private AccountType accountType;
    private Account account;
    private Tariff tariff;
    private Owner owner;
    private Rate rate;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SavingsAccountRepository savingsAccountRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TariffRepository tariffRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private RateRepository rateRepository;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:14")
                    .withInitScript("create_schema_test.sql");

    @DynamicPropertySource
    static void start(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Ошибка если SavingsAccountCreatedDto пустое")
        public void whenOpenSavingsAccountWithCreatedDtoIsNullThenThrowException() throws Exception {
            mockMvc.perform(post("/v1/savingsAccounts")
                            .header("x-user-id", 1)
                            .content("{}"))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Ошибка если SavingsAccountCreatedDto не содержит значения в поле accountId")
        public void whenOpenSavingsAccountWithFieldAccountIdIsNullThenThrowException() throws Exception {
            mockMvc.perform(post("/v1/savingsAccounts")
                            .header("x-user-id", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(objectMapper.writeValueAsString(SavingsAccountCreatedDto.builder()
                                    .tariffName(TARIFF_NAME)
                                    .build())))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Ошибка если SavingsAccountCreatedDto не содержит значения в поле tariffName")
        public void whenOpenSavingsAccountWithFieldTariffNameIsNullThenThrowException() throws Exception {
            mockMvc.perform(post("/v1/savingsAccounts")
                            .header("x-user-id", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(objectMapper.writeValueAsString(SavingsAccountCreatedDto.builder()
                                    .accountId(ID)
                                    .build())))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class PositiveTests {

        @BeforeAll
        public void init() {
            createdDto = SavingsAccountCreatedDto.builder()
                    .accountId(ID)
                    .tariffName(TARIFF_NAME)
                    .build();

            rate = Rate.builder()
                    .id(ID)
                    .interestRate(INTEREST_RATE)
                    .build();
            rateRepository.save(rate);

            tariff = Tariff.builder()
                    .tariffName(TARIFF_NAME)
                    .rate(rate)
                    .rateHistory(RATE_HISTORY)
                    .build();
            tariffRepository.save(tariff);

            owner = Owner.builder()
                    .id(ID)
                    .name(NAME)
                    .build();
            ownerRepository.save(owner);

            accountType = AccountType.builder()
                    .id(ID)
                    .name(NAME)
                    .build();
            typeRepository.save(accountType);

            account = Account.builder()
                    .id(ID)
                    .accountType(accountType)
                    .owner(owner)
                    .number(ACCOUNT_NUMBER)
                    .currency(Currency.USD)
                    .status(AccountStatus.ACTIVE)
                    .build();
            accountRepository.save(account);

            savingsAccount = SavingsAccount.builder()
                    .id(ID)
                    .owner(owner)
                    .account(account)
                    .balance(BALANCE)
                    .number(String.valueOf(NUMBER))
                    .tariff(tariff)
                    .tariffHistory(TARIFF_HISTORY)
                    .build();
            savingsAccountRepository.save(savingsAccount);
        }

        @Test
        @DisplayName("Успех при создании накопительного счета")
        public void whenOpenSavingsAccountThenReturnSavingsAccountDto() throws Exception {
            MvcResult mvcResult = mockMvc.perform(post("/v1/savingsAccounts")
                            .header("x-user-id", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(objectMapper.writeValueAsString(createdDto)))
                    .andExpect(status().isOk())
                    .andReturn();
            SavingsAccountDto result = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(),
                    SavingsAccountDto.class);

            assertNotNull(result);
            assertEquals(createdDto.getAccountId(), result.getAccountId());
            assertEquals(createdDto.getTariffName(), result.getTariffDto().getTariffName());
        }

        @Test
        @DisplayName("Успех при поиске накопительного счета по savingsAccountId")
        public void whenGetSavingsAccountByIdThenReturnSavingsAccountDto() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get("/v1/savingsAccounts/{savingsAccountId}", ID)
                            .header("x-user-id", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk())
                    .andReturn();
            SavingsAccountDto result = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(),
                    SavingsAccountDto.class);

            assertNotNull(result);
            assertEquals(ID, result.getAccountId());
        }

        @Test
        @DisplayName("Успех при поиске накопительного счета по ownerId")
        public void whenGetSavingsAccountByOwnerIdThenReturnSavingsAccountDto() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get("/v1/savingsAccounts/owner/{ownerId}", ID)
                            .header("x-user-id", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk())
                    .andReturn();
            SavingsAccountDto result = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(),
                    SavingsAccountDto.class);

            assertNotNull(result);
        }
    }
}