package faang.school.accountservice.service;

import faang.school.accountservice.dto.Currency;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.BalanceDMS;
import faang.school.accountservice.model.BalanceAudit;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.service.balance.BalanceAuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceDMSAuditServiceTest {

    @Mock
    private BalanceMapper balanceMapper;

    @Mock
    private BalanceAuditRepository balanceAuditRepository;

    @InjectMocks
    private BalanceAuditService balanceAuditService;

    private BalanceDMS balanceDMS;
    private UUID operationId;
    private BalanceAudit balanceAudit;

    @BeforeEach
    void setUp() {
        operationId = UUID.randomUUID();

        balanceDMS = new BalanceDMS();
        balanceDMS.setAccountId(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"));
        balanceDMS.setCurrency(Currency.USD);
        balanceDMS.setClearBalance(new BigDecimal("1000"));
        balanceDMS.setAuthBalance(new BigDecimal("500"));

        balanceAudit = new BalanceAudit();
        balanceAudit.setAccountId(balanceDMS.getAccountId());
        balanceAudit.setPaymentOperationId(operationId);
        balanceAudit.setCurrency(Currency.USD);
        balanceAudit.setClearBalanceChange(new BigDecimal("1000"));
        balanceAudit.setAuthBalanceChange(new BigDecimal("500"));
    }

    @Test
    void givenValidData_whenSetAudit_thenSuccess() {
        when(balanceMapper.toBalanceAudit(balanceDMS, operationId)).thenReturn(balanceAudit);
        when(balanceAuditRepository.save(any(BalanceAudit.class))).thenReturn(balanceAudit);

        balanceAuditService.setAudit(balanceDMS, operationId);

        verify(balanceMapper, times(1)).toBalanceAudit(balanceDMS, operationId);
        verify(balanceAuditRepository, times(1)).save(balanceAudit);
    }
}