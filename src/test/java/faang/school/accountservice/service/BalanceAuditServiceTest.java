package faang.school.accountservice.service;

import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.model.balance.BalanceAudit;
import faang.school.accountservice.repository.BalanceAuditRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceAuditServiceTest {

    @Mock
    private BalanceAuditRepository balanceAuditRepository;

    @Mock
    private BalanceAuditMapper balanceAuditMapper;

    @InjectMocks
    private BalanceAuditService balanceAuditService;


    @Test
    void testSavesAuditWithIncrementedVersion() {

        Balance balance = new Balance();
        BalanceAudit balanceAudit = new BalanceAudit();
        balanceAudit.setVersion(0L);

        when(balanceAuditMapper.toBalanceAudit(balance)).thenReturn(balanceAudit);

        balanceAuditService.createBalanceAudit(balance);

        verify(balanceAuditRepository, times(1)).save(balanceAudit);
        assert balanceAudit.getVersion() == 1;
    }

    @Test
    void testWithAccountSavesAudit() {

        Account account = new Account();
        Balance balance = new Balance();
        BalanceAudit balanceAudit = new BalanceAudit();

        account.setBalance(balance);
        when(balanceAuditMapper.toBalanceAudit(balance)).thenReturn(balanceAudit);

        balanceAuditService.createBalanceAudit(account);

        verify(balanceAuditRepository, times(1)).save(balanceAudit);
    }
}
