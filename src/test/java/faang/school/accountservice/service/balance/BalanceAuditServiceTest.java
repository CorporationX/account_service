package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceAuditDto;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.model.balance.BalanceAudit;
import faang.school.accountservice.repository.BalanceAuditRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BalanceAuditServiceTest {

    @Mock
    private BalanceAuditRepository balanceAuditRepository;

    @Mock
    private BalanceAuditMapper balanceAuditMapper;

    @InjectMocks
    private BalanceAuditService balanceAuditService;

    public BalanceAuditServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAudit() {
        Balance balance = mock(Balance.class);
        Long operationId = 123L;

        BalanceAudit mockAudit = new BalanceAudit();
        when(balanceAuditMapper.toAuditEntity(balance)).thenReturn(mockAudit);
        when(balanceAuditRepository.save(mockAudit)).thenReturn(mockAudit);

        BalanceAudit result = balanceAuditService.createAudit(balance, operationId);

        assertNotNull(result);
        assertEquals(mockAudit, result);
        assertEquals(operationId, mockAudit.getOperationId());
        verify(balanceAuditMapper).toAuditEntity(balance);
        verify(balanceAuditRepository).save(mockAudit);
    }

    @Test
    void testGetAuditHistory() {
        Long accountId = 456L;

        List<BalanceAudit> mockAudits = Arrays.asList(new BalanceAudit(), new BalanceAudit());
        List<BalanceAuditDto> mockAuditDtos = Arrays.asList(new BalanceAuditDto(), new BalanceAuditDto());

        when(balanceAuditRepository.findByAccountId(accountId)).thenReturn(mockAudits);
        when(balanceAuditMapper.toListAuditDto(mockAudits)).thenReturn(mockAuditDtos);

        List<BalanceAuditDto> result = balanceAuditService.getAuditHistory(accountId);

        assertNotNull(result);
        assertEquals(mockAuditDtos, result);
        verify(balanceAuditRepository).findByAccountId(accountId);
        verify(balanceAuditMapper).toListAuditDto(mockAudits);
    }
}
