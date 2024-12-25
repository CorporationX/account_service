package faang.school.accountservice.controller.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.account.AccountDtoCloseBlock;
import faang.school.accountservice.dto.account.AccountDtoFilter;
import faang.school.accountservice.dto.account.AccountDtoOpen;
import faang.school.accountservice.dto.account.AccountDtoResponse;
import faang.school.accountservice.dto.account.AccountDtoVerify;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.jpa.AccountJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountControllerMvcIT {
    @Container
    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
        registry.add("spring.liquibase.contexts", () -> "test");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String URL_PREFIX = "/api/v1/accounts";
    private static final String URL_HEALTH_CHECK = "/health-check";
    private static final String URL_OPEN = "/open";
    private static final String URL_VERIFY = "/verify";
    private static final String URL_ACCOUNT_ID = "/{id}";
    private static final String URL_CLOSE = "/close";
    private static final String URL_BLOCK = "/block";

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountJpaRepository accountJpaRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testHealthCheck_Positive() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX + URL_HEALTH_CHECK)
                        .header("x-user-id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void testOpen_Positive() throws Exception {
        AccountDtoOpen dto = AccountDtoOpen.builder()
                .ownerId(20L)
                .ownerType(OwnerType.PROJECT)
                .accountType(AccountType.SAVINGS)
                .currency(Currency.EUR)
                .notes("Notes")
                .build();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + URL_OPEN)
                        .header("x-user-id", 1L)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        AccountDtoResponse response = objectMapper.readValue(content, AccountDtoResponse.class);
        assertEquals(response.getStatus(), AccountStatus.PENDING);
        assertTrue(response.getAccountNumber().length() >= 12);
        assertEquals(response.getNotes(), dto.getNotes());
    }

    @Test
    void testOpen_invalidOwnerId_Negative() throws Exception {
        AccountDtoOpen dto = AccountDtoOpen.builder()
                .ownerId(-1L)
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post(URL_PREFIX + URL_OPEN)
                        .header("x-user-id", 1L)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testVerify_Positive() throws Exception {
        Account account = accountJpaRepository.findAccountsByStatus(AccountStatus.PENDING).get(0);
        AccountDtoVerify dto = AccountDtoVerify.builder()
                .id(account.getId())
                .build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(URL_PREFIX + URL_VERIFY)
                        .header("x-user-id", 1L)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        AccountDtoResponse response = objectMapper.readValue(content, AccountDtoResponse.class);
        assertEquals(response.getStatus(), AccountStatus.ACTIVE);
    }

    @Test
    void testVerify_Negative() throws Exception {
        AccountDtoVerify dto = AccountDtoVerify.builder().build();
        mockMvc.perform(MockMvcRequestBuilders.put(URL_PREFIX + URL_VERIFY)
                        .header("x-user-id", 1L)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAccount_Positive() throws Exception {
        long id = 17L;
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX + URL_ACCOUNT_ID, id)
                        .header("x-user-id", 1L))
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        AccountDtoResponse response = objectMapper.readValue(content, AccountDtoResponse.class);
        assertEquals((long) response.getId(), id);
        assertEquals("SAVINGS", response.getAccountType().name());
    }

    @Test
    void testGetAccount_invalidId_Negative() throws Exception {
        long id = 0;
        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX + URL_ACCOUNT_ID, id)
                        .header("x-user-id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAccount_notFound_Negative() throws Exception {
        long id = 777L;
        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX + URL_ACCOUNT_ID, id)
                        .header("x-user-id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAccounts_all_Positive() throws Exception {
        AccountDtoFilter dtoFilter = AccountDtoFilter.builder().build();
        int accountsCount = accountRepository.findAll().size();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX)
                        .header("x-user-id", 1L)
                        .content(objectMapper.writeValueAsString(dtoFilter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        AccountDtoResponse[] response = objectMapper.readValue(content, AccountDtoResponse[].class);
        assertEquals(response.length, accountsCount);
    }

    @Test
    void testGetAccounts_withOwnerId_Positive() throws Exception {
        AccountDtoFilter dtoFilter = AccountDtoFilter.builder()
                .ownerIds(List.of(1001L, 1002L))
                .build();
        long accountsCount = accountJpaRepository.findAccountsByOwnerIds(dtoFilter.getOwnerIds()).size();

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get(URL_PREFIX)
                                .header("x-user-id", 1L)
                                .content(objectMapper.writeValueAsString(dtoFilter))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        AccountDtoResponse[] response = objectMapper.readValue(content, AccountDtoResponse[].class);
        assertEquals(response.length, accountsCount);
    }

    @Test
    void testGetAccounts_Negative() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get(URL_PREFIX)
                                .header("x-user-id", 1L)
                                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCloseAccount_withId_Positive() throws Exception {
        Account account = accountJpaRepository.findAccountsByStatus(AccountStatus.ACTIVE).get(0);
        AccountDtoCloseBlock dto = AccountDtoCloseBlock.builder()
                .id(account.getId())
                .build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(URL_PREFIX + URL_CLOSE)
                        .header("x-user-id", 1L)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        AccountDtoResponse response = objectMapper.readValue(content, AccountDtoResponse.class);
        assertSame(response.getStatus(), AccountStatus.CLOSED);
    }

    @Test
    void testCloseAccount_withAccountNumber_Positive() throws Exception {
        Account account = accountJpaRepository.findAccountsByStatus(AccountStatus.ACTIVE).get(0);
        AccountDtoCloseBlock dto = AccountDtoCloseBlock.builder()
                .accountNumber(account.getAccountNumber())
                .build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(URL_PREFIX + URL_CLOSE)
                        .header("x-user-id", 1L)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        AccountDtoResponse response = objectMapper.readValue(content, AccountDtoResponse.class);
        assertSame(response.getStatus(), AccountStatus.CLOSED);
    }

    @Test
    void testCloseAccount_nullInput_Negative() throws Exception {
        AccountDtoCloseBlock dto = AccountDtoCloseBlock.builder()
                .build();
        mockMvc.perform(MockMvcRequestBuilders.put(URL_PREFIX + URL_CLOSE)
                        .header("x-user-id", 1L)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCloseAccount_alreadyClosed_Negative() throws Exception {
        Account account = accountJpaRepository.findAccountsByStatus(AccountStatus.CLOSED).get(0);
        AccountDtoCloseBlock dto = AccountDtoCloseBlock.builder()
                .id(account.getId())
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put(URL_PREFIX + URL_CLOSE)
                        .header("x-user-id", 1L)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isConflict());
    }

    @Test
    void testBlockAccount_withId_Positive() throws Exception {
        Account account = accountJpaRepository.findAccountsByStatus(AccountStatus.ACTIVE).get(0);
        AccountDtoCloseBlock dto = AccountDtoCloseBlock.builder()
                .id(account.getId())
                .build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(URL_PREFIX + URL_BLOCK)
                        .header("x-user-id", 1L)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        AccountDtoResponse response = objectMapper.readValue(content, AccountDtoResponse.class);
        assertSame(response.getStatus(), AccountStatus.BLOCKED);
    }

    @Test
    void testBlockAccount_withAccountNumber_Positive() throws Exception {
        Account account = accountJpaRepository.findAccountsByStatus(AccountStatus.ACTIVE).get(0);
        AccountDtoCloseBlock dto = AccountDtoCloseBlock.builder()
                .accountNumber(account.getAccountNumber())
                .build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(URL_PREFIX + URL_BLOCK)
                        .header("x-user-id", 1L)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        AccountDtoResponse response = objectMapper.readValue(content, AccountDtoResponse.class);
        assertSame(response.getStatus(), AccountStatus.BLOCKED);
    }

    @Test
    void testBlockAccount_nullInput_Negative() throws Exception {
        AccountDtoCloseBlock dto = AccountDtoCloseBlock.builder()
                .build();
        mockMvc.perform(MockMvcRequestBuilders.put(URL_PREFIX + URL_BLOCK)
                        .header("x-user-id", 1L)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound());
    }

    @Test
    void testBlockAccount_alreadyClosed_Negative() throws Exception {
        Account account = accountJpaRepository.findAccountsByStatus(AccountStatus.BLOCKED).get(0);
        AccountDtoCloseBlock dto = AccountDtoCloseBlock.builder()
                .id(account.getId())
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put(URL_PREFIX + URL_BLOCK)
                        .header("x-user-id", 1L)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isConflict());
    }
}