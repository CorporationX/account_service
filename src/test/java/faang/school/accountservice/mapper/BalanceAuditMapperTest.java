package faang.school.accountservice.mapper;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BalanceAuditMapperTest {
    private BalanceAuditMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(BalanceAuditMapper.class);
    }

    @Test
    void testToBalanceAudit() {
        Account account = Account.builder()
                .id(2L)
                .build();

        Balance balance = Balance.builder()
                .account(account)
                .authorizedBalance(new BigDecimal("1000.00"))
                .actualBalance(new BigDecimal("950.00"))
                .balanceVersion(1)
                .build();
        Long transactionId = 987654321L;

        BalanceAudit balanceAudit = mapper.toBalanceAudit(balance, transactionId);

        assertNotNull(balanceAudit);
        assertEquals(transactionId, balanceAudit.getTransactionId());
        assertEquals(balance.getAccount(), balanceAudit.getAccount());
        assertEquals(balance.getBalanceVersion(), balanceAudit.getBalanceVersion());
        assertEquals(balance.getAuthorizedBalance(), balanceAudit.getAuthorizedBalance());
        assertEquals(balance.getActualBalance(), balanceAudit.getActualBalance());
    }
}