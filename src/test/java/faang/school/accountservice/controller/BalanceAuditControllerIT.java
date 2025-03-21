package faang.school.accountservice.controller;

import faang.school.accountservice.AccountServiceApplication;
import faang.school.accountservice.BaseIntegrationTest;
import faang.school.accountservice.enums.AuditEventType;
import faang.school.accountservice.service.AccountService;
import faang.school.accountservice.service.balance.BalanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(classes = AccountServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BalanceAuditControllerIT extends BaseIntegrationTest {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private AccountService accountService;

    @Sql(scripts = {
            "/cleanup-test-data.sql",
            "/test-data-accounts-balances.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/cleanup-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)

    @Test
    public void testGetAudit_Success() {
        balanceService.createBalanceForAccount(1L);
        long auditId = 1L;

        webTestClient.get()
                .uri("/audits/{auditId}", auditId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.balanceAuditId").isEqualTo(auditId)
                .jsonPath("$.userId").isEqualTo(1L)
                .jsonPath("$.auditEventType").isEqualTo(AuditEventType.BALANCE_CREATION.toString());
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
}
