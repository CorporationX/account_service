package faang.school.accountservice.mapper.account;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.Balance;
import faang.school.accountservice.entity.account.BalanceAudit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BalanceAuditMapperTest {

    private BalanceAuditMapper balanceAuditMapper;

    @BeforeEach
    public void setUp() {
        balanceAuditMapper = new BalanceAuditMapper();
    }

    @Test
    public void testToBalanceAudit() {
        Account account = mock(Account.class);
        when(account.getPaymentNumber()).thenReturn("1234567890");

        Balance balance = new Balance();
        balance.setAccount(account);
        balance.setVersion(1L);
        balance.setAuthorisationBalance(BigDecimal.valueOf(500));
        balance.setActualBalance(BigDecimal.valueOf(400));

        BalanceAudit balanceAudit = balanceAuditMapper.toBalanceAudit(balance);

        assertEquals("1234567890", balanceAudit.getAccountNumber());
        assertEquals(1L, balanceAudit.getVersion());
        assertEquals(BigDecimal.valueOf(500), balanceAudit.getAuthorizationBalance());
        assertEquals(BigDecimal.valueOf(400), balanceAudit.getActualBalance());
    }

}