package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceMapper;
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
    BalanceService balanceService;

    @Mock
    BalanceRepository balanceRepository;

    @Mock
    BalanceMapper balanceMapper;

    @Mock
    BalanceValidator balanceValidator;

    private Balance balance;
    private BalanceDto balanceDto;
    private Long balanceId;

    @BeforeEach
    public void setUp() {
        balance = Balance.builder().id(1L).build();
        balanceDto = BalanceDto.builder().id(1L).build();
        balanceId = 1L;
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

        balanceService.createBalance(balanceDto);

        verify(balanceValidator, times(1)).checkIsNull(balanceDto);
        verify(balanceMapper, times(1)).toEntity(balanceDto);
        verify(balanceMapper, times(1)).toDto(balance);
    }

    @Test
    public void testUpdateBalance() {

        when(balanceMapper.toEntity(balanceDto)).thenReturn(balance);
        when(balanceRepository.save(balance)).thenReturn(balance);
        when(balanceMapper.toDto(balance)).thenReturn(balanceDto);

        balanceService.updateBalance(balanceDto);

        verify(balanceValidator, times(1)).checkIsNull(balanceDto);
        verify(balanceValidator, times(1)).checkExistsBalanceInBd(balanceDto);
        verify(balanceMapper, times(1)).toEntity(balanceDto);
        verify(balanceMapper, times(1)).toDto(balance);
        verify(balanceRepository, times(1)).save(balance);
    }
}