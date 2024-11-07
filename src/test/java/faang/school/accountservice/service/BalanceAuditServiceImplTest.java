package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.repository.BalanceAuditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceAuditServiceImplTest {

    @Mock
    private BalanceAuditRepository balanceAuditRepository;

    @InjectMocks
    private BalanceAuditServiceImpl balanceAuditService;

    private Balance balance;
    private BigDecimal authBalance;
    private BigDecimal factBalance;
    private BalanceAudit balanceAudit;

    @BeforeEach
    void setUp() {
        String number = "1234 1234 1234 1234";
        Account account = Account.builder()
                .number(number)
                .build();
        balance = Balance.builder()
                .account(account)
                .build();
        authBalance = BigDecimal.valueOf(100.00);
        factBalance = BigDecimal.valueOf(120.00);
        balanceAudit = BalanceAudit.builder()
                .number(number)
                .curAuthBalance(authBalance.doubleValue())
                .curFactBalance(factBalance.doubleValue())
                .balance(balance)
                .build();
    }

    @Test
    void recordBalanceChange_shouldSaveBalanceAudit() {
        when(balanceAuditRepository.save(any(BalanceAudit.class))).thenReturn(balanceAudit);

        BalanceAudit result = balanceAuditService.recordBalanceChange(authBalance, factBalance, balance);

        verify(balanceAuditRepository).save(any(BalanceAudit.class));
        assertEquals(balanceAudit, result);
    }

    @Test
    void recordBalanceChange_shouldSetCorrectValuesInBalanceAudit() {
        when(balanceAuditRepository.save(any(BalanceAudit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BalanceAudit result = balanceAuditService.recordBalanceChange(authBalance, factBalance, balance);

        assertEquals(authBalance.doubleValue(), result.getCurAuthBalance());
        assertEquals(factBalance.doubleValue(), result.getCurFactBalance());
        assertEquals(balance, result.getBalance());
        verify(balanceAuditRepository).save(result);
    }
}
