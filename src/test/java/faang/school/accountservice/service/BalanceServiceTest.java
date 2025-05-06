package faang.school.accountservice.service;

import faang.school.accountservice.constant.service.BalanceServiceTestConstants;
import faang.school.accountservice.dto.balance.ResponseBalanceDto;
import faang.school.accountservice.mapper.BalanceMapperImpl;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest extends BalanceServiceTestConstants {

    @InjectMocks
    private BalanceService balanceService;

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private AccountRepository accountRepository;

    @Spy
    private BalanceMapperImpl balanceMapper;

    @Test
    void create_ShouldCreate() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(ACCOUNT));
        when(balanceRepository.save(any())).thenReturn(BALANCE);

        ResponseBalanceDto responseBalance = balanceService.create(ACCOUNT_ID);

        assertEquals(BALANCE_ID, responseBalance.getId());
        verify(accountRepository, ONCE).findById(ACCOUNT_ID);
        verify(balanceRepository, ONCE).save(any());
        verify(balanceMapper, ONCE).toDto(BALANCE);
    }

    @Test
    void create_ShouldExceptionWhenAccountNotExists() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> balanceService.create(ACCOUNT_ID));
        verify(accountRepository, ONCE).findById(ACCOUNT_ID);
        verify(balanceRepository, NEVER).save(any());
        verify(balanceMapper, NEVER).toDto(BALANCE);
    }

    @Test
    void find_ShouldFind() {
        when(balanceRepository.findById(BALANCE_ID)).thenReturn(Optional.of(BALANCE));

        ResponseBalanceDto responseBalance = balanceService.find(BALANCE_ID);

        assertEquals(BALANCE_ID, responseBalance.getId());
        verify(balanceRepository, ONCE).findById(BALANCE_ID);
        verify(balanceMapper, ONCE).toDto(BALANCE);
    }

    @Test
    void find_ShouldExceptionWhenBalanceNotExists() {
        balanceNotFound(() -> balanceService.find(BALANCE_ID));
    }

    @Test
    void update_ShouldUpdate() {
        when(balanceRepository.findById(BALANCE_ID)).thenReturn(Optional.of(BALANCE));
        balanceService.update(UPDATE_BALANCE_DTO);

        assertEquals(AUTHORIZATION, BALANCE.getAuthorizationBalance());
        assertEquals(ACTUAL_BALANCE, BALANCE.getActualBalance());
        verify(balanceRepository, ONCE).findById(BALANCE_ID);
        verify(balanceMapper, ONCE).toDto(BALANCE);
    }

    @Test
    void update_ShouldExceptionWhenBalanceNotExists() {
        balanceNotFound(() -> balanceService.update(UPDATE_BALANCE_DTO));
    }

    @Test
    void delete_ShouldDelete() {
        balanceService.delete(BALANCE_ID);
        verify(balanceRepository, ONCE).deleteById(BALANCE_ID);
    }

    private void balanceNotFound(Executable executable) {
        when(balanceRepository.findById(BALANCE_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, executable);

        verify(balanceRepository, ONCE).findById(BALANCE_ID);
        verify(balanceMapper, NEVER).toDto(BALANCE);
    }
}