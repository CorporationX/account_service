package faang.school.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.account.AccountCreateProjectDto;
import faang.school.accountservice.dto.account.AccountCreateUserDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.util.BaseContextTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AccountControllerIT extends BaseContextTest {

    private static final String GET_URL = "/api/v1/accounts/{accountId}";
    private static final String CREATE_ACCOUNT_USER_URL = "/api/v1/accounts/user";
    private static final String CREATE_ACCOUNT_PROJECT_URL = "/api/v1/accounts/project";
    private static final String CLOSE_ACCOUNT_URL = "/api/v1/accounts/close/{accountId}";
    private static final String CONVERT_ACCOUNT_CURRENCY_URL = "/api/v1/accounts/convert/{accountId}";

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        testAccount = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber("1111111111111")
                .type(AccountType.BUSINESS)
                .status(AccountStatus.ACTIVE)
                .userId(1L)
                .currency(Currency.EUR)
                .build();
    }

    @Test
    void testGetAccountById() throws Exception {
        testAccount = accountRepository.save(testAccount);

        mockMvc.perform(get(GET_URL, testAccount.getId()).header("x-user-id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value(Currency.EUR.name()))
                .andExpect(jsonPath("$.number").value("1111111111111"))
                .andExpect(jsonPath("$.type").value(AccountType.BUSINESS.name()))
                .andExpect(jsonPath("$.status").value(AccountStatus.ACTIVE.name()));
    }

    @Test
    void testCreateAccountForUser() throws Exception {
        AccountCreateUserDto dto = AccountCreateUserDto.builder()
                .userId(1L)
                .accountType(AccountType.BUSINESS)
                .currency(Currency.EUR)
                .build();

        String jsonDto = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post(CREATE_ACCOUNT_USER_URL)
                        .header("x-user-id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDto))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value(Currency.EUR.name()))
                .andExpect(jsonPath("$.type").value(AccountType.BUSINESS.name()))
                .andExpect(jsonPath("$.status").value(AccountStatus.ACTIVE.name()));
    }

    @Test
    void testCreateAccountForProject() throws Exception {
        AccountCreateProjectDto dto = AccountCreateProjectDto.builder()
                .projectId(1L)
                .accountType(AccountType.BUSINESS)
                .currency(Currency.EUR)
                .build();

        String jsonDto = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post(CREATE_ACCOUNT_PROJECT_URL)
                        .header("x-user-id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDto))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value(Currency.EUR.name()))
                .andExpect(jsonPath("$.type").value(AccountType.BUSINESS.name()))
                .andExpect(jsonPath("$.status").value(AccountStatus.ACTIVE.name()));
    }

    @Test
    void testCloseAccount() throws Exception {
        testAccount = accountRepository.save(testAccount);

        mockMvc.perform(patch(CLOSE_ACCOUNT_URL, testAccount.getId()).header("x-user-id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(AccountStatus.CLOSED.name()));
    }

    @Test
    void testConvertAccountCurrency() throws Exception {
        testAccount = accountRepository.save(testAccount);

        mockMvc.perform(patch(CONVERT_ACCOUNT_CURRENCY_URL, testAccount.getId())
                        .param("currency", Currency.RUB.name())
                        .header("x-user-id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value(Currency.RUB.name()));
    }
}
