package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.exception.BalanceException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BalanceMapper balanceMapper;

    @InjectMocks
    private BalanceService balanceService;

    @Captor
    private ArgumentCaptor<Balance> balanceCaptor;

    private final long balanceId = 1L;
    private final long accountId = 2L;


    @Test
    public void getBalanceSuccessTest() {
        Balance balance = getBalance();
        BalanceDto balanceDto = getBalanceDto();

        when(balanceRepository.findByAccountId(any())).thenReturn(Optional.of(balance));
        when(balanceMapper.toDto(any())).thenReturn(balanceDto);

        BalanceDto actualResult = balanceService.getBalance(accountId);

        verify(balanceMapper, times(1)).toDto(any());
        verify(balanceRepository, times(1)).findByAccountId(any());

        assertThat(balanceDto.getId()).isEqualTo(actualResult.getId());
        assertThat(balanceDto.getActualBalance()).isEqualTo(actualResult.getActualBalance());
        assertThat(balanceDto.getAuthorizationBalance()).isEqualTo(actualResult.getAuthorizationBalance());
        assertThat(balanceDto.getVersion()).isEqualTo(actualResult.getVersion());
        assertThat(balanceDto.getCreatedAt()).isEqualTo(actualResult.getCreatedAt());
        assertThat(balanceDto.getUpdatedAt()).isEqualTo(actualResult.getUpdatedAt());
        assertThat(balanceDto.getAccountId()).isEqualTo(actualResult.getAccountId());
    }

    @Test
    public void getBalanceFailedTest() {
        EntityNotFoundException entityNotFoundException =
                assertThrows(EntityNotFoundException.class, () -> balanceService.getBalance(accountId)
                );

        verify(balanceMapper, never()).toDto(any());
        verify(balanceRepository, times(1)).findByAccountId(any());

        assertEquals(String.format(BalanceService.BALANCE_NOT_FOUND, accountId), entityNotFoundException.getMessage());
    }

    @Test
    public void createBalanceSuccessTest() {
        Balance balance = getBalance();
        BalanceDto balanceDto = getBalanceDto();

        when(accountRepository.findById(any())).thenReturn(Optional.of(getAccount()));
        when(balanceRepository.save(any())).thenReturn(balance);
        when(balanceMapper.toDto(any())).thenReturn(balanceDto);

        BalanceDto actualResult = balanceService.createBalance(accountId);

        verify(balanceMapper, times(1)).toDto(any());
        verify(accountRepository, times(1)).findById(any());
        verify(balanceRepository, times(1)).save(any());

        assertThat(balanceDto.getId()).isEqualTo(actualResult.getId());
        assertThat(balanceDto.getActualBalance()).isEqualTo(actualResult.getActualBalance());
        assertThat(balanceDto.getAuthorizationBalance()).isEqualTo(actualResult.getAuthorizationBalance());
        assertThat(balanceDto.getVersion()).isEqualTo(actualResult.getVersion());
        assertThat(balanceDto.getCreatedAt()).isEqualTo(actualResult.getCreatedAt());
        assertThat(balanceDto.getUpdatedAt()).isEqualTo(actualResult.getUpdatedAt());
        assertThat(balanceDto.getAccountId()).isEqualTo(actualResult.getAccountId());
    }

    @Test
    public void createBalanceWithExceptionFailTest() {
        when(accountRepository.findById(any())).thenThrow(new EntityNotFoundException(String.format(BalanceService.ACCOUNT_NOT_FOUND, accountId)));

        EntityNotFoundException entityNotFoundException =
                assertThrows(EntityNotFoundException.class, () -> balanceService.createBalance(accountId)
                );

        verify(balanceMapper, never()).toDto(any());
        verify(accountRepository, times(1)).findById(any());
        verify(balanceRepository, never()).save(any());
        assertEquals(String.format(BalanceService.ACCOUNT_NOT_FOUND, accountId), entityNotFoundException.getMessage());
    }

    @Test
    public void updateActualBalanceSuccessTest() {
        Balance balance = getBalance();
        BigDecimal amount = BigDecimal.valueOf(1000);

        when(balanceRepository.findByAccountId(any())).thenReturn(Optional.of(balance));
        when(balanceRepository.save(any())).thenReturn(balance);

        balanceService.updateActualBalance(accountId, amount);

        verify(balanceRepository, times(1)).findByAccountId(any());
        verify(balanceMapper, times(1)).toDto(any());
        verify(balanceRepository, times(1)).save(balanceCaptor.capture());
        assertThat(balanceCaptor.getValue().getActualBalance()).isEqualTo(amount);
    }

    @Test
    public void updateActualBalanceNotFoundFailedTest() {
        EntityNotFoundException entityNotFoundException =
                assertThrows(EntityNotFoundException.class, () -> balanceService.updateActualBalance(accountId, BigDecimal.valueOf(100))
                );

        verify(balanceRepository, times(1)).findByAccountId(any());
        verify(balanceRepository, never()).save(any());
        verify(balanceMapper, never()).toDto(any());

        assertEquals(String.format(BalanceService.BALANCE_NOT_FOUND, accountId), entityNotFoundException.getMessage());
    }

    @Test
    public void updateActualBalanceWithAmountNullFailedTest() {
        Balance balance = getBalance();

        when(balanceRepository.findByAccountId(any())).thenReturn(Optional.of(balance));

        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> balanceService.updateActualBalance(accountId, null)
                );

        verify(balanceRepository, times(1)).findByAccountId(any());
        verify(balanceRepository, never()).save(any());
        verify(balanceMapper, never()).toDto(any());

        assertEquals(String.format(BalanceService.INCORRECT_AMOUNT_VALUE, accountId, null), illegalArgumentException.getMessage());
    }

    @Test
    public void authorizationBalanceSuccessTest() {
        Balance balance = getBalance();
        BigDecimal amount = BigDecimal.valueOf(100);

        when(balanceRepository.findByAccountId(any())).thenReturn(Optional.of(balance));
        when(balanceRepository.save(any())).thenReturn(balance);

        balanceService.authorizationBalance(accountId, amount);

        verify(balanceRepository, times(1)).findByAccountId(any());
        verify(balanceMapper, times(1)).toDto(any());
        verify(balanceRepository, times(1)).save(balanceCaptor.capture());
        assertThat(balanceCaptor.getValue().getActualBalance()).isEqualTo(BigDecimal.valueOf(600));
        assertThat(balanceCaptor.getValue().getAuthorizationBalance()).isEqualTo(amount);
    }

    @Test
    public void authorizationBalanceNotFoundFailedTest() {
        EntityNotFoundException entityNotFoundException =
                assertThrows(EntityNotFoundException.class, () -> balanceService.authorizationBalance(accountId, BigDecimal.valueOf(100))
                );

        verify(balanceRepository, times(1)).findByAccountId(any());
        verify(balanceRepository, never()).save(any());
        verify(balanceMapper, never()).toDto(any());

        assertEquals(String.format(BalanceService.BALANCE_NOT_FOUND, accountId), entityNotFoundException.getMessage());
    }

    @Test
    public void authorizationBalanceWithAmountNullFailedTest() {
        Balance balance = getBalance();

        when(balanceRepository.findByAccountId(any())).thenReturn(Optional.of(balance));

        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> balanceService.authorizationBalance(accountId, null)
                );

        verify(balanceRepository, times(1)).findByAccountId(any());
        verify(balanceRepository, never()).save(any());
        verify(balanceMapper, never()).toDto(any());

        assertEquals(String.format(BalanceService.INCORRECT_AMOUNT_VALUE, accountId, null), illegalArgumentException.getMessage());
    }

    @Test
    public void authorizationBalanceNotEnoughFundsFailedTest() {
        Balance balance = getBalance();
        BigDecimal amount = BigDecimal.valueOf(1000);
        when(balanceRepository.findByAccountId(any())).thenReturn(Optional.of(balance));

        BalanceException balanceException =
                assertThrows(BalanceException.class, () -> balanceService.authorizationBalance(accountId, amount)
                );

        verify(balanceRepository, times(1)).findByAccountId(any());
        verify(balanceRepository, never()).save(any());
        verify(balanceMapper, never()).toDto(any());

        assertEquals(String.format(BalanceService.NOT_ENOUGH_FUNDS, accountId, amount), balanceException.getMessage());
    }

    @Test
    public void updateAuthorizationBalanceSuccessTest() {
        Balance balance = getBalance();
        balance.setAuthorizationBalance(BigDecimal.valueOf(100));
        BigDecimal amount = BigDecimal.valueOf(80);

        when(balanceRepository.findByAccountId(any())).thenReturn(Optional.of(balance));
        when(balanceRepository.save(any())).thenReturn(balance);

        balanceService.updateAuthorizationBalance(accountId, amount);

        verify(balanceRepository, times(1)).findByAccountId(any());
        verify(balanceMapper, times(1)).toDto(any());
        verify(balanceRepository, times(1)).save(balanceCaptor.capture());
        assertThat(balanceCaptor.getValue().getActualBalance()).isEqualTo(BigDecimal.valueOf(700));
        assertThat(balanceCaptor.getValue().getAuthorizationBalance()).isEqualTo(BigDecimal.valueOf(20));
    }

    @Test
    public void updateAuthorizationBalanceNotFoundFailedTest() {
        EntityNotFoundException entityNotFoundException =
                assertThrows(EntityNotFoundException.class, () -> balanceService.updateAuthorizationBalance(accountId, BigDecimal.valueOf(100))
                );

        verify(balanceRepository, times(1)).findByAccountId(any());
        verify(balanceRepository, never()).save(any());
        verify(balanceMapper, never()).toDto(any());

        assertEquals(String.format(BalanceService.BALANCE_NOT_FOUND, accountId), entityNotFoundException.getMessage());
    }

    @Test
    public void updateAuthorizationBalanceWithAmountNullFailedTest() {
        Balance balance = getBalance();

        when(balanceRepository.findByAccountId(any())).thenReturn(Optional.of(balance));

        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> balanceService.updateAuthorizationBalance(accountId, null)
                );

        verify(balanceRepository, times(1)).findByAccountId(any());
        verify(balanceRepository, never()).save(any());
        verify(balanceMapper, never()).toDto(any());

        assertEquals(String.format(BalanceService.INCORRECT_AMOUNT_VALUE, accountId, null), illegalArgumentException.getMessage());
    }

    @Test
    public void updateAuthorizationBalanceNotEnoughFundsFailedTest() {
        Balance balance = getBalance();
        BigDecimal amount = BigDecimal.valueOf(200);
        when(balanceRepository.findByAccountId(any())).thenReturn(Optional.of(balance));

        BalanceException balanceException =
                assertThrows(BalanceException.class, () -> balanceService.updateAuthorizationBalance(accountId, amount)
                );

        verify(balanceRepository, times(1)).findByAccountId(any());
        verify(balanceRepository, never()).save(any());
        verify(balanceMapper, never()).toDto(any());

        assertEquals(String.format(BalanceService.NOT_ENOUGH_FUNDS_ON_AUTHORIZATION_BALANCE, accountId, amount), balanceException.getMessage());
    }

    @Test
    public void clearingBalanceSuccessTest() {
        Balance balance = getBalance();
        balance.setAuthorizationBalance(BigDecimal.valueOf(20));

        when(balanceRepository.findByAccountId(any())).thenReturn(Optional.of(balance));
        when(balanceRepository.save(any())).thenReturn(balance);

        balanceService.clearingBalance(accountId);

        verify(balanceRepository, times(1)).findByAccountId(any());
        verify(balanceMapper, times(1)).toDto(any());
        verify(balanceRepository, times(1)).save(balanceCaptor.capture());
        assertThat(balanceCaptor.getValue().getActualBalance()).isEqualTo(BigDecimal.valueOf(720));
        assertThat(balanceCaptor.getValue().getAuthorizationBalance()).isEqualTo(BigDecimal.valueOf(0.0));
    }

    @Test
    public void clearingBalanceNotFoundFailedTest() {
        EntityNotFoundException entityNotFoundException =
                assertThrows(EntityNotFoundException.class, () -> balanceService.clearingBalance(accountId)
                );

        verify(balanceRepository, times(1)).findByAccountId(any());
        verify(balanceRepository, never()).save(any());
        verify(balanceMapper, never()).toDto(any());

        assertEquals(String.format(BalanceService.BALANCE_NOT_FOUND, accountId), entityNotFoundException.getMessage());
    }

    private Balance getBalance() {
        LocalDateTime now = LocalDateTime.now();

        return Balance.builder()
                .id(balanceId)
                .actualBalance(BigDecimal.valueOf(700))
                .authorizationBalance(BigDecimal.valueOf(0))
                .createdAt(now)
                .updatedAt(now)
                .version(1)
                .account(getAccount())
                .build();
    }

    private Account getAccount() {
        return Account.builder()
                .id(accountId)
                .build();
    }

    private BalanceDto getBalanceDto() {
        LocalDateTime now = LocalDateTime.now();

        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setId(balanceId);
        balanceDto.setActualBalance(BigDecimal.valueOf(800));
        balanceDto.setAuthorizationBalance(BigDecimal.valueOf(200));
        balanceDto.setCreatedAt(now);
        balanceDto.setUpdatedAt(now);
        balanceDto.setVersion(1);
        balanceDto.setAccountId(accountId);

        return balanceDto;
    }
}
