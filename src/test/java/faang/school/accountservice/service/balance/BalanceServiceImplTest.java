package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceServiceImplTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private BalanceMapper balanceMapper;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    private Account account;
    private Balance balance;
    private BalanceDto balanceDto;

    @BeforeEach
    void setUp() {
        account = new Account();
        balance = new Balance();
        balance.setAccount(account);
        balance.setActualBalance(BigDecimal.ZERO);
        balance.setAuthorizationBalance(BigDecimal.ZERO);

        balanceDto = new BalanceDto();
    }

    @Test
    void testCreateBalance() {
        when(balanceRepository.save(any(Balance.class))).thenReturn(balance);

        balanceService.createBalance(1l);

        verify(balanceRepository).save(any(Balance.class));
        verifyNoMoreInteractions(balanceRepository);
    }

    @Test
    void testUpdateBalance() {
        when(balanceMapper.toEntity(any(BalanceDto.class))).thenReturn(balance);
        when(balanceRepository.save(any(Balance.class))).thenReturn(balance);
        when(balanceMapper.toDto(any(Balance.class))).thenReturn(balanceDto);

        BalanceDto result = balanceService.updateBalance(balanceDto);

        assertNotNull(result);
        verify(balanceMapper).toEntity(balanceDto);
        verify(balanceRepository).save(balance);
        verify(balanceMapper).toDto(balance);
    }

    @Test
    void testGetBalance() {
        when(balanceRepository.findById(anyLong())).thenReturn(Optional.of(balance));
        when(balanceMapper.toDto(any(Balance.class))).thenReturn(balanceDto);

        BalanceDto result = balanceService.getBalance(1L);

        assertNotNull(result);
        verify(balanceRepository).findById(1L);
        verify(balanceMapper).toDto(balance);
    }

    @Test
    void testGetBalance_NotFound() {
        when(balanceRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> balanceService.getBalance(1L));
    }
}
