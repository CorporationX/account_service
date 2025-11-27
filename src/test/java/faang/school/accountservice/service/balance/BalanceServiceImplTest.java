package faang.school.accountservice.service.balance;


import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.balance.BalanceResponseDto;
import faang.school.accountservice.dto.balance.BalanceUpdateDto;
import faang.school.accountservice.exception.AccountValidateException;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.exception.ForbiddenException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.AccountStatusType;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceImplTest {

    @Mock
    BalanceRepository balanceRepository;

    @Mock
    BalanceMapper balanceMapper;

    @Mock
    AccountServiceImpl accountService;

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    BalanceServiceImpl service;

    @Captor
    ArgumentCaptor<Balance> balanceCaptor;

    @Test
    public void createBalance_responseDtoHasFrozen_shouldThrowAccountValidateException() {
        AccountDto dto = AccountDto.builder()
                .status(AccountStatusType.FROZEN)
                .build();

        when(accountService.getAccount(1L)).thenReturn(dto);

        assertThrows(AccountValidateException.class,
                () -> service.createBalance(1L));
    }

    @Test
    public void createBalance_responseDtoHasClosed_shouldThrowAccountValidateException() {
        AccountDto dto = AccountDto.builder()
                .status(AccountStatusType.CLOSED)
                .build();

        when(accountService.getAccount(1L)).thenReturn(dto);

        assertThrows(AccountValidateException.class,
                () -> service.createBalance(1L));
    }

    @Test
    public void createBalance_entityNotFound_shouldThrowEntityNotFoundException() {
        AccountDto dto = AccountDto.builder()
                .status(AccountStatusType.ACTIVE)
                .build();
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());
        when(accountService.getAccount(1L)).thenReturn(dto);

        assertThrows(EntityNotFoundException.class,
                () -> service.createBalance(1L));
    }

    @Test
    public void createBalance_accountHasBalance_shouldThrowForbiddenException() {
        AccountDto dto = AccountDto.builder()
                .status(AccountStatusType.ACTIVE)
                .build();

        Balance balance = new Balance();
        Account account = new Account();
        account.setBalance(balance);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountService.getAccount(1L)).thenReturn(dto);

        assertThrows(ForbiddenException.class,
                () -> service.createBalance(1L));
    }

    @Test
    public void createBalance_responseDto_shouldResponseAccountDto() {
        long accountId = 1L;
        AccountDto accountDto = AccountDto.builder()
                .status(AccountStatusType.ACTIVE)
                .build();

        Account account = new Account();
        account.setId(accountId);

        LocalDateTime now = LocalDateTime.now();
        Balance savedBalance = new Balance();
        savedBalance.setId(UUID.randomUUID());
        savedBalance.setAccount(account);
        savedBalance.setAuthorizationAmount(0L);
        savedBalance.setActualAmount(0L);
        savedBalance.setCreatedAt(now);
        savedBalance.setUpdatedAt(now);

        BalanceResponseDto expectedDto = BalanceResponseDto.builder()
                .authorizationAmount(0L)
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(accountService.getAccount(accountId)).thenReturn(accountDto);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(balanceRepository.save(any(Balance.class))).thenReturn(savedBalance);
        when(balanceMapper.toDto(savedBalance)).thenReturn(expectedDto);

        BalanceResponseDto actualDto = service.createBalance(accountId);

        verify(balanceRepository).save(balanceCaptor.capture());
        Balance capturedBalance = balanceCaptor.getValue();

        assertNotNull(capturedBalance);
        assertEquals(account, capturedBalance.getAccount());
        assertEquals(0L, capturedBalance.getAuthorizationAmount());
        assertNull(capturedBalance.getCreatedAt());
        assertNull(capturedBalance.getUpdatedAt());

        assertEquals(expectedDto.authorizationAmount(), actualDto.authorizationAmount());
        assertEquals(expectedDto.createdAt(), actualDto.createdAt());
        assertEquals(expectedDto.updatedAt(), actualDto.updatedAt());

        verify(balanceMapper).toDto(savedBalance);
    }

    @Test
    public void updateBalance_responseDtoHasFrozen_shouldThrowAccountValidateException() {
        AccountDto dto = AccountDto.builder()
                .status(AccountStatusType.FROZEN)
                .build();
        BalanceUpdateDto updateDto = new BalanceUpdateDto(0L);
        when(accountService.getAccount(1L)).thenReturn(dto);

        assertThrows(AccountValidateException.class,
                () -> service.updateBalance(1L, updateDto));
    }

    @Test
    public void updateBalance_responseDtoHasClosed_shouldThrowAccountValidateException() {
        AccountDto dto = AccountDto.builder()
                .status(AccountStatusType.CLOSED)
                .build();
        BalanceUpdateDto updateDto = new BalanceUpdateDto(0L);
        when(accountService.getAccount(1L)).thenReturn(dto);

        assertThrows(AccountValidateException.class,
                () -> service.updateBalance(1L, updateDto));
    }

    @Test
    public void updateBalance_success_shouldUpdateAndReturnDto() {
        long accountId = 1L;
        BalanceUpdateDto updateDto = new BalanceUpdateDto(100L);
        AccountDto accountDto = AccountDto.builder()
                .status(AccountStatusType.ACTIVE)
                .build();
        Balance existingBalance = new Balance();
        existingBalance.setId(UUID.randomUUID());
        existingBalance.setAuthorizationAmount(200L);
        existingBalance.setActualAmount(0L);
        existingBalance.setCreatedAt(LocalDateTime.now().minusDays(1));
        existingBalance.setUpdatedAt(LocalDateTime.now().minusDays(1));
        LocalDateTime newUpdatedAt = LocalDateTime.now();
        Balance savedBalance = new Balance();
        savedBalance.setId(existingBalance.getId());
        savedBalance.setAuthorizationAmount(300L);
        savedBalance.setActualAmount(0L);
        savedBalance.setCreatedAt(existingBalance.getCreatedAt());
        savedBalance.setUpdatedAt(newUpdatedAt);
        BalanceResponseDto expectedDto = BalanceResponseDto.builder()
                .authorizationAmount(300L)
                .createdAt(existingBalance.getCreatedAt())
                .updatedAt(newUpdatedAt)
                .build();

        when(accountService.getAccount(accountId)).thenReturn(accountDto);
        when(balanceRepository.findByAccountId(accountId)).thenReturn(existingBalance);
        when(balanceRepository.save(any(Balance.class))).thenReturn(savedBalance);
        when(balanceMapper.toDto(savedBalance)).thenReturn(expectedDto);

        BalanceResponseDto actualDto = service.updateBalance(accountId, updateDto);

        verify(balanceRepository).save(balanceCaptor.capture());
        Balance capturedBalance = balanceCaptor.getValue();

        assertEquals(300L, capturedBalance.getAuthorizationAmount());
        assertEquals(expectedDto, actualDto);
    }

    @Test
    public void getBalance_success_shouldReturnDto() {
        long accountId = 1L;
        Balance balance = new Balance();
        balance.setId(UUID.randomUUID());
        balance.setAuthorizationAmount(500L);
        balance.setCreatedAt(LocalDateTime.now().minusDays(1));
        balance.setUpdatedAt(LocalDateTime.now());
        BalanceResponseDto expectedDto = BalanceResponseDto.builder()
                .authorizationAmount(500L)
                .createdAt(balance.getCreatedAt())
                .updatedAt(balance.getUpdatedAt())
                .build();
        when(balanceRepository.findByAccountId(accountId)).thenReturn(balance);
        when(balanceMapper.toDto(balance)).thenReturn(expectedDto);

        BalanceResponseDto actualDto = service.getBalance(accountId);

        assertEquals(expectedDto, actualDto);
        verify(balanceRepository).findByAccountId(accountId);
        verify(balanceMapper).toDto(balance);
    }

}