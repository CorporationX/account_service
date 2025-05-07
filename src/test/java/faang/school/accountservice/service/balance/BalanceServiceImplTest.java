package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.BalanceRequestDto;
import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.BalanceAlreadyExistsException;
import faang.school.accountservice.exception.BalanceConflictException;
import faang.school.accountservice.exception.BalanceNotFoundException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static faang.school.accountservice.messages.ErrorMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceServiceImplTest {
    @InjectMocks
    private BalanceServiceImpl balanceService;

    @Mock
    private BalanceRepository balanceRepository;
    @Mock
    private AccountRepository accountRepository;

    @Spy
    private BalanceMapper balanceMapper = Mappers.getMapper(BalanceMapper.class);

    @Captor
    private ArgumentCaptor<Balance> balanceCaptor;

    private Balance balance;
    private Balance updatedBalance;
    private Account account;
    private String accountNumber;
    private BalanceRequestDto requestDto;

    @BeforeEach
    void setUp() {
        balance = new Balance();
        account = new Account();
        requestDto = new BalanceRequestDto();
        accountNumber = "A12345";

        requestDto.setAccountNumber(accountNumber);

        account.setAccountNumber(accountNumber);

        updatedBalance = Balance.builder()
                .account(account)
                .authorizationBalance(BigDecimal.valueOf(85))
                .factualBalance(BigDecimal.valueOf(90))
                .build();

        balance = Balance.builder()
                .account(account)
                .authorizationBalance(BigDecimal.valueOf(90))
                .factualBalance(BigDecimal.valueOf(100))
                .build();
    }

    //Positive

    @Test
    void testUpdateBalance() {
        requestDto.setAuthorizationBalance(BigDecimal.valueOf(85));
        requestDto.setFactualBalance(BigDecimal.valueOf(90));
        when(balanceRepository.findBalanceByAccountNumber(accountNumber)).thenReturn(Optional.of(balance));

        balanceService.updateBalance(requestDto);

        verify(balanceRepository, times(1)).save(balanceCaptor.capture());
        assertEquals(updatedBalance, balanceCaptor.getValue());
    }

    @Test
    void testCreateBalance() {
        requestDto.setAuthorizationBalance(BigDecimal.valueOf(90));
        requestDto.setFactualBalance(BigDecimal.valueOf(100));
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        balanceService.createBalance(requestDto);

        verify(balanceRepository, times(1)).save(balanceCaptor.capture());
        Balance captured = balanceCaptor.getValue();
        assertNotNull(captured.getCreatedAt());
        assertTrue(captured.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(captured.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(5)));
        assertEquals(accountNumber, captured.getAccount().getAccountNumber());
        assertEquals(balance.getAuthorizationBalance(), captured.getAuthorizationBalance());
        assertEquals(balance.getFactualBalance(), captured.getFactualBalance());
    }

    @Test
    void testGetBalanceByAccountNumber() {
        when(balanceRepository.findBalanceByAccountNumber(accountNumber)).thenReturn(Optional.of(balance));

        BalanceResponseDto responseDto = balanceService.getBalanceByAccountNumber(accountNumber);
        assertNotNull(responseDto);
        assertEquals(responseDto.getAuthorizationBalance(), balance.getAuthorizationBalance());
        assertEquals(responseDto.getFactualBalance(), balance.getFactualBalance());
        assertEquals(responseDto.getAccountNumber(), balance.getAccount().getAccountNumber());
    }

    //Negative
    @Test
    void testUpdateBalance_BalanceNotFound() {
        when(balanceRepository.findBalanceByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        BalanceNotFoundException exception = assertThrows(BalanceNotFoundException.class,
                () -> balanceService.updateBalance(requestDto));
        assertEquals(String.format(BALANCE_NOT_FOUND, accountNumber), exception.getMessage());
    }

    @Test
    void testUpdateBalance_OptimisticLockException() {
        requestDto.setAuthorizationBalance(BigDecimal.valueOf(85));
        requestDto.setFactualBalance(BigDecimal.valueOf(90));
        when(balanceRepository.findBalanceByAccountNumber(accountNumber)).thenReturn(Optional.of(balance));
        when(balanceRepository.save(balanceCaptor.capture())).thenThrow(new OptimisticLockException("Optimistic lock failure"));

        BalanceConflictException exception = assertThrows(BalanceConflictException.class,
                () -> balanceService.updateBalance(requestDto));
        verify(balanceRepository, times(1)).save(balanceCaptor.getValue());
        assertEquals(BALANCE_CONFLICT_ERROR, exception.getMessage());
    }

    @Test
    void testCreateBalance_AccountNotFound() {
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class,
                () -> balanceService.createBalance(requestDto));
        assertEquals(String.format(ACCOUNT_NOT_FOUND, accountNumber), exception.getMessage());
    }

    @Test
    void testCreateBalance_BalanceAlreadyExists() {
        requestDto.setAuthorizationBalance(BigDecimal.valueOf(90));
        requestDto.setFactualBalance(BigDecimal.valueOf(100));
        account.setBalance(balance);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        BalanceAlreadyExistsException exception = assertThrows(BalanceAlreadyExistsException.class,
                () -> balanceService.createBalance(requestDto));
        assertEquals(String.format(BALANCE_EXISTS_ERROR, accountNumber), exception.getMessage());
    }

    @Test
    void testGetBalanceByAccountNumber_BalanceNotFound() {
        when(balanceRepository.findBalanceByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        BalanceNotFoundException exception = assertThrows(BalanceNotFoundException.class,
                () -> balanceService.getBalanceByAccountNumber(accountNumber));
        assertEquals(String.format(BALANCE_NOT_FOUND, accountNumber), exception.getMessage());
    }
}