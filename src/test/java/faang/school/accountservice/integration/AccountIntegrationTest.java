package faang.school.accountservice.integration;

import faang.school.accountservice.dto.OpenAccountRequest;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.util.BaseContextTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AccountIntegrationTest extends BaseContextTest {

    @Autowired
    private AccountRepository accountRepository;

    private static final Long TEST_USER_ID = 1L;
    private static final String USER_ID_HEADER = "x-user-id";

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    void testOpenAccount_Success() throws Exception {
        // Given
        OpenAccountRequest request = new OpenAccountRequest(
                TEST_USER_ID,
                OwnerType.USER,
                AccountType.PERSONAL_CHECKING,
                Currency.USD
        );

        // When
        MvcResult result = mockMvc.perform(post("/api/v1/accounts")
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.account_number").exists())
                .andExpect(jsonPath("$.owner_id").value(TEST_USER_ID))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.version").value(0))
                .andReturn();

        // Then
        String response = result.getResponse().getContentAsString();
        assertThat(response).contains("account_number");

        // Verify in database
        assertThat(accountRepository.count()).isEqualTo(1);
    }

    @Test
    void testGetAccount_Success() throws Exception {
        // Given
        Account account = createTestAccount();

        // When & Then
        mockMvc.perform(get("/api/v1/accounts/" + account.getId())
                        .header(USER_ID_HEADER, TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(account.getId()))
                .andExpect(jsonPath("$.account_number").value(account.getAccountNumber()))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void testBlockAccount_Success() throws Exception {
        // Given
        Account account = createTestAccount();

        // When & Then
        mockMvc.perform(patch("/api/v1/accounts/" + account.getId() + "/block")
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\": \"Suspicious activity\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"))
                .andExpect(jsonPath("$.version").value(0));

        // Verify in database
        Account updatedAccount = accountRepository.findById(account.getId()).orElseThrow();
        assertThat(updatedAccount.getStatus()).isEqualTo(AccountStatus.BLOCKED);
        assertThat(updatedAccount.getVersion()).isEqualTo(1L);
    }

    @Test
    void testFreezeAccount_Success() throws Exception {
        // Given
        Account account = createTestAccount();

        // When & Then
        mockMvc.perform(patch("/api/v1/accounts/" + account.getId() + "/freeze")
                        .header(USER_ID_HEADER, TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FROZEN"))
                .andExpect(jsonPath("$.version").value(0));
    }

    @Test
    void testUnfreezeAccount_Success() throws Exception {
        // Given
        Account account = createTestAccount();
        account.setStatus(AccountStatus.FROZEN);
        accountRepository.save(account);

        // When & Then
        mockMvc.perform(patch("/api/v1/accounts/" + account.getId() + "/unfreeze")
                        .header(USER_ID_HEADER, TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void testCloseAccount_Success() throws Exception {
        // Given
        Account account = createTestAccount();
        account.setBalance(BigDecimal.ZERO); // Ensure zero balance
        accountRepository.save(account);

        // When & Then
        mockMvc.perform(patch("/api/v1/accounts/" + account.getId() + "/close")
                        .header(USER_ID_HEADER, TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"))
                .andExpect(jsonPath("$.closed_at").exists());
    }

    @Test
    void testGetOwnerAccounts_Success() throws Exception {
        // Given
        createTestAccount();
        createTestAccount();

        // When & Then
        mockMvc.perform(get("/api/v1/accounts/owner/" + TEST_USER_ID)
                        .header(USER_ID_HEADER, TEST_USER_ID)
                        .param("owner_type", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testOptimisticLocking() throws Exception {
        // Given
        Account account = createTestAccount();
        Long accountId = account.getId();

        // Simulate concurrent update by manually incrementing version
        Account accountCopy1 = accountRepository.findById(accountId).orElseThrow();
        Account accountCopy2 = accountRepository.findById(accountId).orElseThrow();

        // First update succeeds
        accountCopy1.setStatus(AccountStatus.FROZEN);
        accountRepository.save(accountCopy1);

        // Second update should trigger optimistic lock exception
        accountCopy2.setStatus(AccountStatus.BLOCKED);

        try {
            accountRepository.save(accountCopy2);
            assertThat(false).isTrue(); // Force failure
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("Row was updated or deleted by another transaction");
        }
    }

    private Account createTestAccount() {
        return accountRepository.save(Account.builder()
                .accountNumber(generateUniqueAccountNumber())
                .ownerId(TEST_USER_ID)
                .ownerType(OwnerType.USER)
                .accountType(AccountType.PERSONAL_CHECKING)
                .currency(Currency.USD)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .build());
    }

    private String generateUniqueAccountNumber() {
        return String.format("%020d", System.nanoTime() % 100000000000000000L);
    }
}