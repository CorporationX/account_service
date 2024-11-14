package faang.school.accountservice.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.client.ProjectServiceClient;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.dto.account.AccountResponseDto;
import faang.school.accountservice.dto.account.OpenAccountDto;
import faang.school.accountservice.dto.project.ProjectDto;
import faang.school.accountservice.dto.user.UserDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.integration.config.RedisPostgresTestContainers;
import faang.school.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static faang.school.accountservice.enums.payment.Currency.USD;
import static faang.school.accountservice.enums.account.AccountStatus.ACTIVE;
import static faang.school.accountservice.enums.account.AccountStatus.BLOCKED;
import static faang.school.accountservice.enums.account.AccountStatus.CLOSED;
import static faang.school.accountservice.enums.account.AccountType.DEBIT;
import static faang.school.accountservice.util.fabrics.AccountFabric.buildAccountDefault;
import static faang.school.accountservice.util.fabrics.UserDtoFabric.buildUserDtoDefault;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/test-sql/insert-default-accounts-and-balances.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "/test-sql/truncate-balance-account.sql", executionPhase = AFTER_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("testLiquibaseRedis")
@SpringBootTest
public class AccountControllerIntegrationTest extends RedisPostgresTestContainers {
    private static final UUID SOURCE_ACCOUNT_ID = UUID.fromString("065977b1-2f8d-47d5-a2a7-c88671a3c5a3");
    private static final UUID TARGET_ACCOUNT_ID = UUID.fromString("f6309d7b-22bd-4b18-a4fa-29a6bdd502e8");
    private static final UUID SOURCE_BALANCE_ID = UUID.fromString("4cc8cd27-9c53-4e4c-8f44-de6a6d7182c0");
    private static final UUID TARGET_BALANCE_ID = UUID.fromString("bd4a870b-8ffa-4919-a1a4-57c0cb1138a3");
    private static final long USER_ID = 1L;
    private static final long PROJECT_ID = 1L;
    private static final String PROJECT_NAME = "Project name";
    private static final String URL = "/accounts";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    private UserServiceClient userServiceClient;

    @MockBean
    private ProjectServiceClient projectServiceClient;

    @Test
    void testOpenAccount_forUser_successful() throws Exception {
        OpenAccountDto openAccountDto = new OpenAccountDto(USER_ID, null, DEBIT, USD);
        UserDto userDto = buildUserDtoDefault(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);

        ResultActions result = mockMvc.perform(post(URL)
                        .header("x-user-id", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(openAccountDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.number").isNotEmpty())
                .andExpect(jsonPath("$.userId").value(USER_ID))
                .andExpect(jsonPath("$.projectId").isEmpty())
                .andExpect(jsonPath("$.type").value(DEBIT.toString()))
                .andExpect(jsonPath("$.currency").value(USD.toString()))
                .andExpect(jsonPath("$.status").value(ACTIVE.toString()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        String accountJson = result.andReturn().getResponse().getContentAsString();
        AccountResponseDto accountResponseDto = objectMapper.readValue(accountJson, AccountResponseDto.class);

        assertThat(accountRepository.findById(accountResponseDto.getId())).isNotEmpty();
    }

    @Test
    void testOpenAccount_forObject_successful() throws Exception {
        OpenAccountDto openAccountDto = new OpenAccountDto(null, PROJECT_ID, DEBIT, USD);
        ProjectDto projectDto = new ProjectDto(PROJECT_ID, PROJECT_NAME);
        when(projectServiceClient.getProject(PROJECT_ID)).thenReturn(projectDto);

        ResultActions result = mockMvc.perform(post(URL)
                        .header("x-user-id", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(openAccountDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.number").isNotEmpty())
                .andExpect(jsonPath("$.userId").isEmpty())
                .andExpect(jsonPath("$.projectId").value(PROJECT_ID))
                .andExpect(jsonPath("$.type").value(DEBIT.toString()))
                .andExpect(jsonPath("$.currency").value(USD.toString()))
                .andExpect(jsonPath("$.status").value(ACTIVE.toString()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        String accountJson = result.andReturn().getResponse().getContentAsString();
        AccountResponseDto accountResponseDto = objectMapper.readValue(accountJson, AccountResponseDto.class);

        assertThat(accountRepository.findById(accountResponseDto.getId())).isNotEmpty();
    }

    @Test
    void testGetAccountById_successful() throws Exception {
//        Account account = buildAccountDefault(USER_ID);
//        accountRepository.save(account);
//        UUID accountId = accountRepository.findAll().get(0).getId();

        mockMvc.perform(get(URL + "/" + SOURCE_ACCOUNT_ID)
                        .header("x-user-id", 1L))
                .andExpect(jsonPath("$.id").value(SOURCE_ACCOUNT_ID.toString()));
    }

    @Test
    void testCloseAccount_successful() throws Exception {
        Account account = buildAccountDefault(USER_ID);
        accountRepository.save(account);
        UUID accountId = accountRepository.findAll().get(0).getId();

        mockMvc.perform(put(URL + "/" + accountId + "/close")
                        .header("x-user-id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId.toString()))
                .andExpect(jsonPath("$.status").value(CLOSED.toString()));
    }

    @Test
    void testBlockAccount_successful() throws Exception {
        Account account = buildAccountDefault(USER_ID);
        accountRepository.save(account);
        UUID accountId = accountRepository.findAll().get(0).getId();

        mockMvc.perform(put(URL + "/" + accountId + "/block")
                        .header("x-user-id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId.toString()))
                .andExpect(jsonPath("$.status").value(BLOCKED.toString()));
    }
}
