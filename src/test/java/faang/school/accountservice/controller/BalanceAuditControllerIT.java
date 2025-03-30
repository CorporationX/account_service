package faang.school.accountservice.controller;

import faang.school.accountservice.AccountServiceApplication;
import faang.school.accountservice.BaseIntegrationTest;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.Money;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.enums.AuditEventType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.service.AccountService;
import faang.school.accountservice.service.balance.BalanceService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = AccountServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BalanceAuditControllerIT extends BaseIntegrationTest {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private BalanceAuditRepository balanceAuditRepository;

    @MockBean
    private UserContext userContext;

    @Sql(scripts = {
            "/cleanup-test-data.sql",
            "/test-data-accounts-balances.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void testGetAudit_Success() {
        when(userContext.getUserId()).thenReturn(1L);

        balanceService.createBalanceForAccount(1L);
        long auditId = awaitCreatedAuditFromDatabase();

        webTestClient.get()
                .uri("/audits/{auditId}", auditId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.balanceAuditId").isEqualTo(auditId)
                .jsonPath("$.userId").isEqualTo(1L)
                .jsonPath("$.auditEventType").isEqualTo(AuditEventType.BALANCE_CREATION.toString());

    }

    @Sql(scripts = {
            "/cleanup-test-data.sql",
            "/test-data-accounts-balances.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void testGetAuditAfterAdditionBalance_Success() {
        when(userContext.getUserId()).thenReturn(1L);
        UUID balanceId = UUID.fromString("baa4fdc1-8327-4cb0-b102-f59ff9cbf5d1");
        Money testMoney = new Money(new BigDecimal(133), Currency.USD);

        balanceService.topUpCurrentBalance(balanceId, testMoney);
        long auditId = awaitCreatedAuditFromDatabase();

        webTestClient.get()
                .uri("/audits/{auditId}", auditId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.balanceAuditId").isEqualTo(auditId)
                .jsonPath("$.userId").isEqualTo(1L)
                .jsonPath("$.currentAuthAmount").isEqualTo(100)
                .jsonPath("$.currentFactAmount").isEqualTo(233)
                .jsonPath("$.auditEventType").isEqualTo(AuditEventType.BALANCE_ADDITION.toString());
    }

    @Test
    public void testGetAudit_InvalidAuditId() {
        long invalidAuditId = -5L;

        webTestClient.get()
                .uri("/audits/{auditId}", invalidAuditId)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testGetAudit_MissingUserHeader() {
        WebTestClient clientWithoutUserHeader = webTestClient.mutate()
                .defaultHeader("x-user-id", "")
                .build();

        long auditId = 1L;

        clientWithoutUserHeader.get()
                .uri("/audits/{auditId}", auditId)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    private long awaitCreatedAuditFromDatabase() {
        Awaitility.await()
                .atMost(10L, TimeUnit.SECONDS)
                .pollInterval(100L, TimeUnit.MILLISECONDS)
                .until(() -> balanceAuditRepository.findFirstByOrderByAuditedAtDesc().isPresent());

        BalanceAudit audit = balanceAuditRepository.findFirstByOrderByAuditedAtDesc().orElseThrow();
        return audit.getId();
    }
}
