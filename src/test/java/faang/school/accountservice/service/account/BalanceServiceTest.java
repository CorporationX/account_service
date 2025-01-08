package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.BalanceDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.Balance;
import faang.school.accountservice.mapper.account.BalanceMapper;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.repository.account.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private BalanceRepository balanceRepository;
    @Mock
    private BalanceMapper balanceMapper;
    @InjectMocks
    private BalanceService balanceService;

    @Test
    public void shouldReturnBalanceForExistingAccountId() {
        Balance balance = new Balance();
        when(balanceRepository.findByAccount_Id(1L)).thenReturn(Optional.of(balance));

        balanceService.getBalanceByAccount(1L);

        verify(balanceRepository).findByAccount_Id(1L);
        verify(balanceMapper).toDto(balance);
    }

    @Test
    public void shouldThrowExceptionWhenAccountIdIsNotFound() {
        when(balanceRepository.findByAccount_Id(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = Assertions.assertThrows(EntityNotFoundException.class,
                () -> balanceService.getBalanceByAccount(1L));
        assertEquals("Account or associated balance doesn't exist", thrown.getMessage());
    }

    @Test
    public void shouldCreateBalanceWithExistingAccount() {
        Account account = new Account();
        Balance balance = new Balance().setAccount(account);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(balanceRepository.save(any(Balance.class))).thenReturn(balance);

        balanceService.createBalanceForAccount(1L);

        ArgumentCaptor<Balance> argumentCaptor = ArgumentCaptor.forClass(Balance.class);
        verify(balanceRepository).save(argumentCaptor.capture());
        verify(balanceMapper).toDto(balance);
        Assertions.assertEquals(account, argumentCaptor.getValue().getAccount());
    }

    @Test
    public void shouldThrowExceptionWhenAccountIsNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> balanceService.createBalanceForAccount(1L));
        assertEquals("Account doesn't exist", thrown.getMessage());
    }

    @Test
    public void shouldUpdateBalanceForExistingId() {
        BalanceDto balanceDto = BalanceDto.builder()
                .id(1L)
                .authorisationBalance(BigDecimal.valueOf(1))
                .actualBalance(BigDecimal.valueOf(2))
                .build();
        Balance balance = new Balance();
        when(balanceRepository.findById(1L)).thenReturn(Optional.of(balance));
        when(balanceRepository.save(any(Balance.class))).thenReturn(balance);

        balanceService.updateBalanceForAccount(balanceDto);

        verify(balanceRepository).findById(1L);
        verify(balanceRepository).save(balance);
        verify(balanceMapper).toDto(balance);
    }

    @Test
    public void shouldThrowExceptionWhenBalanceIsNotFound() {
        BalanceDto balanceDto = BalanceDto.builder()
                .id(1L)
                .build();
        when(balanceRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> balanceService.updateBalanceForAccount(balanceDto));
        assertEquals("Balance doesn't exist", thrown.getMessage());
    }
}