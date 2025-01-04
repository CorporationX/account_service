package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.repository.BalanceAuditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceAuditServiceTest {

    @Mock
    private  BalanceAuditRepository balanceAuditRepository;

    @Mock
    private  BalanceAuditMapper balanceAuditMapper;

    @InjectMocks
    private BalanceAuditService balanceAuditService;

    @Test
    void testCreateBalanceAudit() {
        Balance balance = Balance.builder()
                .authorizedBalance(new BigDecimal("1000.00"))
                .actualBalance(new BigDecimal("950.00"))
                .balanceVersion(1)
                .build();

        Account account = Account.builder()
                .id(1L)
                .balance(balance)
                .build();

        balance.setAccount(account);
        Long transactionId = 321L;

        BalanceAudit balanceAudit = BalanceAudit.builder()
                .account(account)
                .authorizedBalance(new BigDecimal("1000.00"))
                .actualBalance(new BigDecimal("950.00"))
                .balanceVersion(1)
                .transactionId(transactionId)
                .build();

        when(balanceAuditMapper.toBalanceAudit(balance, transactionId)).thenReturn(balanceAudit);

        balanceAuditService.createBalanceAudit(account, transactionId);

        verify(balanceAuditMapper, times(1)).toBalanceAudit(balance, transactionId);
        verify(balanceAuditRepository, times(1)).save(balanceAudit);
    }
}