package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.account.Balance;
import faang.school.accountservice.entity.account.BalanceAudit;
import faang.school.accountservice.mapper.account.BalanceAuditMapper;
import faang.school.accountservice.repository.account.BalanceAuditRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class BalanceAuditServiceTest {

    @InjectMocks
    private BalanceAuditService balanceAuditService;

    @Mock
    private BalanceAuditRepository balanceAuditRepository;
    @Mock
    private BalanceAuditMapper balanceAuditMapper;

    @Test
    void testCreate(){
        BalanceAudit balanceAudit = BalanceAudit.builder().id(1L).build();
        Balance balance = new Balance();
        when(balanceAuditMapper.toBalanceAudit(balance)).thenReturn(balanceAudit);
        balanceAuditService.create(balance);
        verify(balanceAuditRepository, times(1)).save(balanceAudit);

    }



}