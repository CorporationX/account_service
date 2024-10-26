package faang.school.accountservice.integration.audit.service;

import faang.school.accountservice.integration.audit.IntegrationTestBase;
import faang.school.accountservice.model.entity.Account;
import faang.school.accountservice.model.entity.Balance;
import faang.school.accountservice.model.entity.BalanceAudit;
import faang.school.accountservice.model.enums.OperationType;
import faang.school.accountservice.repository.AuditRepository;
import faang.school.accountservice.service.impl.audit.BalanceAuditServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Sql("classpath:sql/dataBalanceAudit.sql")
class BalanceAuditServiceImplIT extends IntegrationTestBase {
    @Autowired
    private BalanceAuditServiceImpl balanceAuditService;

    @Autowired
    private AuditRepository auditRepository;

    @Test
    @DisplayName("Запуск контейнера")
    void testContainerIsRunning() {
        assertThat(container.isRunning()).as("PostgreSQL контейнер не запущен!").isTrue();
    }

    @Test
    @DisplayName("Сохранение balanceAudit в БД")
    void saveBalanceAudit() {
        Account account = Account.builder()
                .id(3)
                .build();
        Balance balance = Balance.builder()
                .account(account)
                .currentAuthorizationBalance(BigDecimal.valueOf(2))
                .currentActualBalance(BigDecimal.valueOf(3232))
                .version(3)
                .id(3)
                .build();
        BalanceAudit balanceAudit = BalanceAudit.builder()
                .id(3)
                .balance(balance)
                .authorizationBalance(BigDecimal.valueOf(2))
                .actualBalance(BigDecimal.valueOf(3232))
                .version(3)
                .type(OperationType.BOOKING)
                .build();

        balanceAuditService.saveBalanceAudit(balance, OperationType.BOOKING);

        var result = auditRepository.findById(3L).get();

        assertThat(result).isNotNull();
        assertThat(balanceAudit).usingRecursiveComparison()
                .ignoringFields("auditAt")
                .isEqualTo(result);
    }
}