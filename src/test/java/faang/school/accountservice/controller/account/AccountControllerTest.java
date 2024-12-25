package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.Currency;
import faang.school.accountservice.entity.account.OwnerType;
import faang.school.accountservice.entity.account.Status;
import faang.school.accountservice.entity.account.Type;
import faang.school.accountservice.repository.account.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class AccountControllerTest {
    @Autowired
    private AccountController accountController;

    @Autowired
    private AccountRepository accountRepository;

    @Container
    private static PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:13.6").withReuse(true);

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgresContainer.getJdbcUrl());
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }


    @Test
    void testGetAccount() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/owners/1")
                        .header("x-user-id", 1)
                        .param("ownerType", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void testGetAccountBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/owners/1")
                        .header("x-user-id", 1)
                        .param("ownerType", "-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testOpenNewAccount() throws Exception {
        CreateAccountDto createAccountDto = new CreateAccountDto();
        createAccountDto.setType(Type.FOREX_ACCOUNT);
        createAccountDto.setCurrency(Currency.EUR);
        createAccountDto.setOwnerType(OwnerType.USER);
        createAccountDto.setOwnerId(1);

        String json = objectMapper.writeValueAsString(createAccountDto);

        mockMvc.perform(post("/api/v1/accounts")
                        .header("x-user-id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerId").value("1"))
                .andExpect(jsonPath("$.ownerType").value("USER"));

    }

    @Test
    void testOpenNewAccountBadRequest() throws Exception {
        CreateAccountDto createAccountDto = new CreateAccountDto();
        String json = objectMapper.writeValueAsString(createAccountDto);

        mockMvc.perform(post("/api/v1/accounts")
                        .header("x-user-id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

    }

    @Test
    void testChangeStatus() throws Exception {
        Account account = new Account();
        account.setOwnerType(OwnerType.USER);
        account.setOwnerId(11);
        account.setType(Type.FOREX_ACCOUNT);
        account.setCurrency(Currency.EUR);
        account.setStatus(Status.ACTIVE);
        account.setAccountNumber("09876543212345");
        accountRepository.save(account);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/accounts/1")
                        .header("x-user-id", 1)
                        .param("status", "CLOSED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"));
    }

    @Test
    void testChangeStatusBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/accounts/-1")
                        .header("x-user-id", 1)
                        .param("status", "CLOSED"))
                .andExpect(status().isBadRequest());
    }

}