package faang.school.accountservice.integration.controller.balance;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.integration.config.RedisPostgresTestContainers;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static faang.school.accountservice.util.fabrics.AccountFabric.buildAccountDefault;
import static faang.school.accountservice.util.fabrics.BalanceFabric.buildBalance;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("testLiquibaseRedis")
@SpringBootTest
public class BalanceControllerIntegrationTest extends RedisPostgresTestContainers {
    private static final long USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    @Test
    void testFindBalanceByAccountId_successful() throws Exception {
        Account account = buildAccountDefault(USER_ID);
        accountRepository.save(account);
        UUID accountId = accountRepository.findAll().get(0).getId();
        Balance balance = buildBalance(account);
        balanceRepository.save(balance);

        mockMvc.perform(get("/balances/" + accountId)
                        .header("x-user-id", USER_ID))
                .andExpect(status().isOk());
    }
}
