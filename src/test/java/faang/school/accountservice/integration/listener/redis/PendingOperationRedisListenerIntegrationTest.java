package faang.school.accountservice.integration.listener.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.balance.response.CheckingAccountBalance;
import faang.school.accountservice.dto.listener.pending.OperationMessage;
import faang.school.accountservice.entity.auth.payment.AuthPayment;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.integration.config.TestContainersConfig;
import faang.school.accountservice.integration.config.listener.RedisCheckingBalanceListener;
import faang.school.accountservice.repository.balance.AuthPaymentRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.UUID;

import static faang.school.accountservice.enums.Currency.USD;
import static faang.school.accountservice.enums.auth.payment.AuthPaymentStatus.ACTIVE;
import static faang.school.accountservice.enums.pending.AccountBalanceStatus.SUFFICIENT_FUNDS;
import static faang.school.accountservice.enums.pending.Category.OTHER;
import static faang.school.accountservice.enums.pending.OperationStatus.PENDING;
import static faang.school.accountservice.util.fabrics.OperationMessageFabric.buildOperationMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Sql(scripts = "/test-sql/create-account-no-constraint-and-balance-auth-payment.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "/test-sql/insert-default-accounts-and-balances.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "/test-sql/drop-account-balance-auth-payment.sql", executionPhase = AFTER_TEST_METHOD)
@ActiveProfiles("testNoLiquibase")
@SpringBootTest
@Import(RedisCheckingBalanceListener.class)
public class PendingOperationRedisListenerIntegrationTest extends TestContainersConfig {
    private static final UUID OPERATION_ID = UUID.randomUUID();
    private static final UUID SOURCE_ACCOUNT_ID = UUID.fromString("065977b1-2f8d-47d5-a2a7-c88671a3c5a3");
    private static final UUID TARGET_ACCOUNT_ID = UUID.fromString("f6309d7b-22bd-4b18-a4fa-29a6bdd502e8");
    private static final UUID SOURCE_BALANCE_ID = UUID.fromString("4cc8cd27-9c53-4e4c-8f44-de6a6d7182c0");
    private static final UUID TARGET_BALANCE_ID = UUID.fromString("bd4a870b-8ffa-4919-a1a4-57c0cb1138a3");
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(20);
    private static final Currency CURRENCY = USD;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private AuthPaymentRepository authPaymentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisCheckingBalanceListener redisCheckingBalanceListener;

    @Test
    void testSaveEvent_pendingStatus_successful() throws JsonProcessingException {
        OperationMessage operation = buildOperationMessage(OPERATION_ID, SOURCE_ACCOUNT_ID, TARGET_ACCOUNT_ID,
                AMOUNT, CURRENCY, OTHER, PENDING);
        String json = objectMapper.writeValueAsString(operation);
        redisTemplate.convertAndSend("pending_operation", json);

        sleep(100);

        AuthPayment authPayments = authPaymentRepository.findAll().get(0);

        assertThat(authPayments.getStatus()).isEqualTo(ACTIVE);
        assertThat(authPayments.getSourceBalance().getId()).isEqualTo(SOURCE_BALANCE_ID);
        assertThat(authPayments.getTargetBalance().getId()).isEqualTo(TARGET_BALANCE_ID);

        Balance sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();

        assertThat(sourceBalance.getAuthBalance()).isEqualTo(AMOUNT);

        String checkingAccountBalanceJson = redisCheckingBalanceListener.getReceivedMessage();
        CheckingAccountBalance checkingAccountBalance = objectMapper.readValue(checkingAccountBalanceJson,
                CheckingAccountBalance.class);
        assertThat(checkingAccountBalance.getSourceAccountId()).isEqualTo(SOURCE_ACCOUNT_ID);
        assertThat(checkingAccountBalance.getStatus()).isEqualTo(SUFFICIENT_FUNDS);
    }

    void sleep(long millisecond) {
        try {
            Thread.sleep(millisecond);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
