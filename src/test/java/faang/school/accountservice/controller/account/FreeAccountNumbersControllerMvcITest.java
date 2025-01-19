package faang.school.accountservice.controller.account;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.model.account.freeaccounts.FreeAccountNumber;
import faang.school.accountservice.model.account.sequence.AccountSeq;
import faang.school.accountservice.repository.account.AccountNumberPrefixRepository;
import faang.school.accountservice.repository.account.freeaccounts.FreeAccountRepository;
import faang.school.accountservice.repository.account.sequence.AccountNumbersSequenceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FreeAccountNumbersControllerMvcITest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountNumberPrefixRepository accountNumberPrefixRepository;
    @Autowired
    private FreeAccountRepository freeAccountRepository;
    @Autowired
    private AccountNumbersSequenceRepository accountNumbersSequenceRepository;

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

    @Test
    void generateFreeAccountNumbersWithBatchSizeTest_noInitialSequenceCounter() throws Exception {
        AccountType accountType = AccountType.SAVINGS;
        int accountNumberLength = 16;
        int batchSize = 5;
        String accountNumberPrefix = accountNumberPrefixRepository.findAccountNumberPrefixByType(accountType);

        mockMvc.perform(post("/api/v1/free-accounts/batchSize")
                        .header("x-user-id", 1)
                        .param("accountType", accountType.name())
                        .param("accountLength", String.valueOf(accountNumberLength))
                        .param("quantity", String.valueOf(batchSize)))
                .andExpect(status().isOk());

        Optional<AccountSeq> accountSequence = accountNumbersSequenceRepository.findByType(accountType);
        assertTrue(accountSequence.isPresent());
        assertEquals(accountType, accountSequence.get().getType());
        assertEquals(batchSize + 1, accountSequence.get().getCounter());

        assertEquals(batchSize, freeAccountRepository.countFreeAccountNumberByType(accountType));
        String accountNumber = freeAccountRepository.findAll().get(0).getAccountNumber();
        assertEquals(accountNumberLength, accountNumber.length());
        assertEquals(accountNumberPrefix, accountNumber.substring(0, 4));
        assertEquals("2200000000000001", accountNumber);
        assertEquals("2200000000000005", freeAccountRepository.findAll().get(batchSize - 1).getAccountNumber());
    }

    @Test
    void generateFreeAccountNumbersWithBatchSizeTest_hasInitialSequenceCounter() throws Exception {
        AccountType accountType = AccountType.SAVINGS;
        int accountNumberLength = 16;
        int batchSize = 5;
        String accountNumberPrefix = accountNumberPrefixRepository.findAccountNumberPrefixByType(accountType);

        createFreeAccountNumbers(1);
        mockMvc.perform(post("/api/v1/free-accounts/batchSize")
                        .header("x-user-id", 1)
                        .param("accountType", accountType.name())
                        .param("accountLength", String.valueOf(accountNumberLength))
                        .param("quantity", String.valueOf(batchSize)))
                .andExpect(status().isOk());

        Optional<AccountSeq> accountSequence = accountNumbersSequenceRepository.findByType(accountType);
        assertTrue(accountSequence.isPresent());
        assertEquals(accountType, accountSequence.get().getType());
        assertEquals(batchSize + 2, accountSequence.get().getCounter());

        assertEquals(batchSize + 1, freeAccountRepository.countFreeAccountNumberByType(accountType));
        String accountNumber = freeAccountRepository.findAll().get(batchSize).getAccountNumber();
        assertEquals(accountNumberLength, accountNumber.length());
        assertEquals(accountNumberPrefix, accountNumber.substring(0, 4));
        assertEquals("2200000000000002", freeAccountRepository.findAll().get(1).getAccountNumber());
        assertEquals("2200000000000006", accountNumber);
    }

    @Test
    void generateFreeAccountNumbersWithBatchSizeTest_invalidAccountNrLength() throws Exception {
        AccountType accountType = AccountType.SAVINGS;
        int batchSize = 5;

        mockMvc.perform(post("/api/v1/free-accounts/batchSize")
                        .header("x-user-id", 1)
                        .param("accountType", accountType.name())
                        .param("accountLength", String.valueOf(11))
                        .param("quantity", String.valueOf(batchSize)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid account number length"));

        mockMvc.perform(post("/api/v1/free-accounts/batchSize")
                        .header("x-user-id", 1)
                        .param("accountType", accountType.name())
                        .param("accountLength", String.valueOf(21))
                        .param("quantity", String.valueOf(batchSize)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid account number length"));

        Optional<AccountSeq> accountSequence = accountNumbersSequenceRepository.findByType(accountType);
        assertFalse(accountSequence.isPresent());
        assertEquals(0, freeAccountRepository.countFreeAccountNumberByType(accountType));
    }

    @Test
    void generateFreeAccountNumbersWithLimit_noInitialSequenceCounter() throws Exception {
        AccountType accountType = AccountType.SAVINGS;
        int accountNumberLength = 16;
        int limit = 5;
        String accountNumberPrefix = accountNumberPrefixRepository.findAccountNumberPrefixByType(accountType);

        mockMvc.perform(post("/api/v1/free-accounts/limit")
                        .header("x-user-id", 1)
                        .param("accountType", accountType.name())
                        .param("accountLength", String.valueOf(accountNumberLength))
                        .param("limit", String.valueOf(limit)))
                .andExpect(status().isOk());

        Optional<AccountSeq> accountSequence = accountNumbersSequenceRepository.findByType(accountType);
        assertTrue(accountSequence.isPresent());
        assertEquals(accountType, accountSequence.get().getType());
        assertEquals(limit + 1, accountSequence.get().getCounter());

        assertEquals(limit, freeAccountRepository.countFreeAccountNumberByType(accountType));
        String accountNumber = freeAccountRepository.findAll().get(0).getAccountNumber();
        assertEquals(accountNumberLength, accountNumber.length());
        assertEquals(accountNumberPrefix, accountNumber.substring(0, 4));
        assertEquals("2200000000000001", accountNumber);
        assertEquals("2200000000000005", freeAccountRepository.findAll().get(limit - 1).getAccountNumber());
    }

    @Test
    void generateFreeAccountNumbersWithLimit_noAccountsCreated() throws Exception {
        AccountType accountType = AccountType.SAVINGS;
        int accountNumberLength = 16;
        int limit = 1;
        String accountNumberPrefix = accountNumberPrefixRepository.findAccountNumberPrefixByType(accountType);

        createFreeAccountNumbers(1);
        mockMvc.perform(post("/api/v1/free-accounts/limit")
                        .header("x-user-id", 1)
                        .param("accountType", accountType.name())
                        .param("accountLength", String.valueOf(accountNumberLength))
                        .param("limit", String.valueOf(limit)))
                .andExpect(status().isOk());

        Optional<AccountSeq> accountSequence = accountNumbersSequenceRepository.findByType(accountType);
        assertTrue(accountSequence.isPresent());
        assertEquals(accountType, accountSequence.get().getType());
        assertEquals(limit + 1, accountSequence.get().getCounter());

        assertEquals(limit, freeAccountRepository.countFreeAccountNumberByType(accountType));
        String accountNumber = freeAccountRepository.findAll().get(0).getAccountNumber();
        assertEquals(accountNumberLength, accountNumber.length());
        assertEquals(accountNumberPrefix, accountNumber.substring(0, 4));
        assertEquals("2200000000000001", accountNumber);
    }

    @Test
    void processAccountNumber_noFreeAccounts() throws Exception {
        AccountType accountType = AccountType.SAVINGS;
        String expectedAccountNumber = "2200000000000001";

        mockMvc.perform(post("/api/v1/free-accounts/process/SAVINGS")
                        .header("x-user-id", 1))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedAccountNumber));

        Optional<AccountSeq> accountSequence = accountNumbersSequenceRepository.findByType(accountType);
        assertTrue(accountSequence.isPresent());
        assertEquals(2L, accountSequence.get().getCounter());

        assertEquals(0, freeAccountRepository.countFreeAccountNumberByType(accountType));
        assertEquals(Collections.EMPTY_LIST, freeAccountRepository.findAll());
    }

    @Test
    void processAccountNumber_FreeAccountsExist() throws Exception {
        AccountType accountType = AccountType.SAVINGS;
        String expectedAccountNumber = "2200000000000001";
        createFreeAccountNumbers(3);

        mockMvc.perform(post("/api/v1/free-accounts/process/SAVINGS")
                        .header("x-user-id", 1))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedAccountNumber));

        Optional<AccountSeq> accountSequence = accountNumbersSequenceRepository.findByType(accountType);
        assertTrue(accountSequence.isPresent());
        assertEquals(4L, accountSequence.get().getCounter());

        assertEquals(2, freeAccountRepository.countFreeAccountNumberByType(accountType));
        String accountNumber = freeAccountRepository.findAll().get(0).getAccountNumber();
        assertEquals("2200000000000002", accountNumber);
    }

    @AfterEach
    void cleanUp() {
        freeAccountRepository.deleteAll();
        accountNumbersSequenceRepository.deleteAll();
    }

    private void createFreeAccountNumbers(long count) {
        for (int i = 1; i <= count; i++) {
            freeAccountRepository.save(new FreeAccountNumber(AccountType.SAVINGS, "220000000000000" + i));
        }
        accountNumbersSequenceRepository.save(new AccountSeq(AccountType.SAVINGS, count + 1, 0));
    }
}
