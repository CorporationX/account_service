package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.repository.BalanceAuditRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
    @DisplayName("Create balance audit success")
    void testCreateBalanceAuditSuccess() {
        Account mockAccount = new Account();
        Balance mockBalance = new Balance();
        mockBalance.setAuthorizedBalance(new BigDecimal("1000.00"));
        mockBalance.setActualBalance(new BigDecimal("950.00"));
        mockAccount.setBalance(mockBalance);

        Long mockTransactionId = 123L;

        BalanceAudit mockBalanceAudit = new BalanceAudit();
        when(balanceAuditMapper.toBalanceAudit(mockBalance, mockTransactionId)).thenReturn(mockBalanceAudit);

        balanceAuditService.createBalanceAudit(mockAccount, mockTransactionId);

        verify(balanceAuditMapper, times(1)).toBalanceAudit(mockBalance, mockTransactionId);
        verify(balanceAuditRepository, times(1)).save(mockBalanceAudit);
    }

    @Test
    @DisplayName("Create balance audit with null account")
    void testCreateBalanceAuditWithNullAccount() {
        Long mockTransactionId = 123L;

        assertThrows(NullPointerException.class, () -> {
            balanceAuditService.createBalanceAudit(null, mockTransactionId);
        });

        verifyNoInteractions(balanceAuditMapper, balanceAuditRepository);
    }
}
