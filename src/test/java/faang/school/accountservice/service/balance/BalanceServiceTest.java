package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.mapper.balance.BalanceMapperImpl;
import faang.school.accountservice.repository.balance.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {

    @InjectMocks
    private BalanceService balanceService;

    @Mock
    private BalanceRepository balanceRepository;

    @Spy
    BalanceMapperImpl balanceMapper;

    private Balance balance;

    @BeforeEach
    void setUp() {
        balance = new Balance();
        balance.setId(1L);
        balance.setAccount(new Account());
        balance.setActualBalance(1000L);
        balance.setAuthorisationBalance(500L);
        balance.setCreatedAt(LocalDateTime.now());
        balance.setUpdatedAt(LocalDateTime.now());
        balance.setVersion(1L);
    }

    @Test
    void testCreateBalance() {

        when(balanceRepository.save(any(Balance.class))).thenReturn(balance);

        BalanceDto result = balanceService.createBalance(new Account(), 50L, 100L);
        
        assertNotNull(result);
        verify(balanceRepository, times(1)).save(any(Balance.class));
        verify(balanceMapper, times(1)).toDto(balance);
    }

    @Test
    void testUpdateBalanceSuccess() {


        when(balanceRepository.findById(balance.getId())).thenReturn(Optional.of(balance));
        when(balanceRepository.save(any(Balance.class))).thenReturn(balance);

        BalanceDto balanceDto = balanceService.updateBalance(balance.getId(), 500L, 1000L);

        assertNotNull(balanceDto);
        verify(balanceRepository, times(1)).findById(balance.getId());
        verify(balanceRepository, times(1)).save(any(Balance.class));
        verify(balanceMapper, times(1)).toDto(balance);
    }

    @Test
    void testUpdateBalanceNotFound() {
        when(balanceRepository.findById(balance.getId())).thenThrow(IllegalArgumentException.class);

        assertThrows(RuntimeException.class,
                () -> balanceService.updateBalance(balance.getId(), 1000L, 500L));
    }

    @Test
    void testGetBalanceSuccess() {
        when(balanceRepository.findById(balance.getId())).thenReturn(Optional.of(balance));

        BalanceDto balanceDto = balanceService.getBalance(balance.getId());

        assertNotNull(balanceDto);
        verify(balanceRepository, times(1)).findById(balance.getId());
        verify(balanceMapper, times(1)).toDto(balance);
    }

    @Test
    void testGetBalanceNotFound() {
        when(balanceRepository.findById(balance.getId())).thenThrow(IllegalArgumentException.class);

        assertThrows(RuntimeException.class,
                () -> balanceService.getBalance(balance.getId()));
    }
}
