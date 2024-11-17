package faang.school.accountservice.integration.controller.balance;

import faang.school.accountservice.integration.config.RedisPostgresTestContainers;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/test-sql/insert-default-accounts-and-balances.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "/test-sql/truncate-balance-account.sql", executionPhase = AFTER_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("testLiquibaseRedis")
@SpringBootTest
public class BalanceControllerIntegrationTest extends RedisPostgresTestContainers {
    private static final UUID SOURCE_ACCOUNT_ID = UUID.fromString("065977b1-2f8d-47d5-a2a7-c88671a3c5a3");
    private static final long USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testFindBalanceByAccountId_successful() throws Exception {
        mockMvc.perform(get("/balances/" + SOURCE_ACCOUNT_ID)
                        .header("x-user-id", USER_ID))
                .andExpect(status().isOk());
    }
}
