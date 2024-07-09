package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.validator.BalanceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {

    @InjectMocks
    private BalanceService balanceService;

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private BalanceMapper balanceMapper;

    @Mock
    private BalanceValidator balanceValidator;

    @Mock
    private BalanceAuditRepository balanceAuditRepository;

    private Balance balance;
    private BalanceDto balanceDto;
    private BalanceAudit balanceAudit;
    private Long balanceId;
    private Long operationId;

    @BeforeEach
    public void setUp() {
        balance = Balance.builder().id(1L).build();
        balanceDto = BalanceDto.builder().id(1L).build();
        balanceAudit = BalanceAudit.builder().id(1L).balanceVersion(5L).build();
        balanceId = 1L;
        operationId = 77L;
    }

    @Test
    public void testGetBalance() {
        when(balanceRepository.findById(balanceId)).thenReturn(Optional.of(balance));
        when(balanceMapper.toDto(balance)).thenReturn(balanceDto);

        balanceService.getBalance(balanceId);

        verify(balanceValidator, times(1)).checkIsNull(balanceId);
        verify(balanceRepository, times(1)).findById(balanceId);
        verify(balanceMapper, times(1)).toDto(balance);
    }

    @Test
    public void testCreateBalance() {
        when(balanceMapper.toEntity(balanceDto)).thenReturn(balance);
        when(balanceRepository.save(balance)).thenReturn(balance);
        when(balanceMapper.toDto(balance)).thenReturn(balanceDto);
        when(balanceMapper.toBalanceAudit(balance, operationId)).thenReturn(balanceAudit);

        balanceService.createBalance(balanceDto, operationId);

        verify(balanceValidator, times(1)).checkAbsenceBalanceByAccountIdInBd(balanceDto);
        verify(balanceMapper, times(1)).toEntity(balanceDto);
        verify(balanceRepository, times(1)).save(balance);
        verify(balanceMapper, times(1)).toBalanceAudit(balance, operationId);
        verify(balanceAuditRepository, times(1)).save(balanceAudit);
        verify(balanceMapper, times(1)).toDto(balance);
    }

    @Test
    public void testUpdateBalance() {
        when(balanceRepository.save(balance)).thenReturn(balance);
        when(balanceMapper.toDto(balance)).thenReturn(balanceDto);
        when(balanceRepository.findById(balanceId)).thenReturn(Optional.of(balance));
        when(balanceMapper.toBalanceAudit(balance, operationId)).thenReturn(balanceAudit);

        balanceService.updateBalance(balanceDto, operationId);

        verify(balanceValidator, times(1)).checkExistsBalanceByIdInBd(balanceDto);
        verify(balanceRepository, times(1)).findById(balanceId);
        verify(balanceMapper, times(1)).updateBalanceFromDto(balanceDto, balance);
        verify(balanceRepository, times(1)).save(balance);
        verify(balanceMapper, times(1)).toBalanceAudit(balance, operationId);
        verify(balanceAuditRepository, times(1)).save(balanceAudit);
        verify(balanceMapper, times(1)).toDto(balance);
    }
}