package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountBalanceDto;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.dto.TransactionDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.AccountOwnerType;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.AccountWithdrawalException;
import faang.school.accountservice.exception.IllegalAccountAccessException;
import faang.school.accountservice.exception.InvalidAccountStatusException;
import faang.school.accountservice.mapper.AccountMapperImpl;
import faang.school.accountservice.publisher.AccountEventPublisher;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Spy
    private AccountMapperImpl accountMapper;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountEventPublisher accountEventPublisher;

    @InjectMocks
    private AccountService accountService;

    private CreateAccountDto createDto;

    @Test
    @DisplayName("Create account success")
    void testCreateAccount_Success() {
        createDto = new CreateAccountDto(AccountOwnerType.PROJECT, "Project owner", AccountType.CURRENT, Currency.USD);
        Long ownerId = 10L;

        AccountDto result = accountService.createAccount(createDto, ownerId);

        verify(accountRepository, times(1)).save(any(Account.class));
        verify(accountEventPublisher, times(1)).publish(any(AccountDto.class));

        assertNotNull(result);
        assertEquals(AccountStatus.ACTIVE, result.status());
        assertEquals(20, result.accountNumber().length());
    }

    @Test
    @DisplayName("Get accounts success: account found")
    void testGetAccounts_AccountFound_Success() {
        AccountOwnerType ownerType = AccountOwnerType.PROJECT;
        Long ownerId = 10L;
        Account account = Account.builder().ownerType(ownerType).ownerId(ownerId).build();
        List<Account> accounts = List.of(account);

        when(accountRepository.findByOwnerTypeAndOwnerId(ownerType, ownerId)).thenReturn(accounts);

        List<AccountDto> result = accountService.getAccounts(ownerType, ownerId);

        verify(accountRepository, times(1)).findByOwnerTypeAndOwnerId(ownerType, ownerId);
        verify(accountMapper, times(1)).toDto(accounts);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ownerId, result.get(0).ownerId());
    }

    @Test
    @DisplayName("Get accounts success: accounts not found")
    void testGetAccounts_AccountNotFound_Success() {
        AccountOwnerType ownerType = AccountOwnerType.PROJECT;
        Long ownerId = 10L;
        List<Account> accounts = List.of();

        when(accountRepository.findByOwnerTypeAndOwnerId(ownerType, ownerId)).thenReturn(accounts);

        List<AccountDto> result = accountService.getAccounts(ownerType, ownerId);

        verify(accountRepository, times(1)).findByOwnerTypeAndOwnerId(ownerType, ownerId);
        verify(accountMapper, times(1)).toDto(accounts);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Update account success")
    void testUpdateAccount_Success() {
        String accountNumber = "ACC0123456789";
        AccountStatus newStatus = AccountStatus.INACTIVE;
        Long ownerId = 10L;
        Account account = Account.builder().accountNumber(accountNumber).status(AccountStatus.ACTIVE).ownerId(ownerId).build();

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        AccountDto result = accountService.updateAccountStatus(accountNumber, ownerId, newStatus);

        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
        verify(accountRepository, times(1)).save(account);
        verify(accountEventPublisher, times(1)).publish(any(AccountDto.class));

        assertNotNull(result);
        assertEquals(newStatus, result.status());
    }

    @Test
    @DisplayName("Update account fail: account not found")
    void testUpdateAccount_WrongAccountNumber_Fail() {
        String accountNumber = "ACC0123456789";
        AccountStatus newStatus = AccountStatus.INACTIVE;
        Long ownerId = 10L;

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                accountService.updateAccountStatus(accountNumber, ownerId, newStatus));

        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
        verify(accountRepository, never()).save(any(Account.class));

        assertEquals(String.format("Account with number %s doesn't exist", accountNumber), ex.getMessage());
    }

    @Test
    @DisplayName("Update account fail: invalid account owner")
    void testUpdateAccount_InvalidAccountOwner_Fail() {
        String accountNumber = "ACC0123456789";
        AccountStatus newStatus = AccountStatus.INACTIVE;
        Long ownerId = 10L;
        Account account = Account.builder().accountNumber(accountNumber).status(AccountStatus.ACTIVE).ownerId(1L).build();

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        IllegalAccountAccessException ex = assertThrows(IllegalAccountAccessException.class, () ->
                accountService.updateAccountStatus(accountNumber, ownerId, newStatus)
        );

        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
        verify(accountRepository, never()).save(any(Account.class));

        assertEquals(String.format("Owner with id %d doesn't have access to the account %s", ownerId, account.getAccountNumber()), ex.getMessage());
    }

    @Test
    @DisplayName("Update account fail: same account status")
    void testUpdateAccount_SameAccountStatus_Fail() {
        String accountNumber = "ACC0123456789";
        AccountStatus newStatus = AccountStatus.ACTIVE;
        Long ownerId = 10L;
        Account account = Account.builder().accountNumber(accountNumber).status(AccountStatus.ACTIVE).ownerId(10L).build();

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        InvalidAccountStatusException ex = assertThrows(InvalidAccountStatusException.class, () ->
                accountService.updateAccountStatus(accountNumber, ownerId, newStatus)
        );

        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
        verify(accountRepository, times(0)).save(any(Account.class));

        assertEquals(String.format("Account with number %s already has status %s", accountNumber, account.getStatus()), ex.getMessage());
    }

    @Test
    @DisplayName("Deposit account success: valid input")
    void testDeposit_Success() {
        TransactionDto transactionDto = new TransactionDto("ACC123456789", BigDecimal.valueOf(100.25));
        AccountBalanceDto accountBalanceDto = new AccountBalanceDto("ACC123456789", BigDecimal.valueOf(110.25), null);
        Account account = Account.builder()
                .accountNumber(transactionDto.accountNumber())
                .balance(Balance.builder()
                        .actualBalance(BigDecimal.valueOf(10))
                        .build())
                .build();
        when(accountRepository.findByAccountNumber(transactionDto.accountNumber())).thenReturn(Optional.of(account));

        AccountBalanceDto result = accountService.deposit(transactionDto);

        verify(accountRepository, times(1)).save(account);
        verify(accountRepository, times(1)).findByAccountNumber(transactionDto.accountNumber());

        assertEquals(accountBalanceDto, result);
        assertEquals(BigDecimal.valueOf(110.25), account.getBalance().getActualBalance());
    }

    @Test
    @DisplayName("Deposit account fail: account not found")
    void testDeposit_AccountNotFound_Fail() {
        TransactionDto transactionDto = new TransactionDto("ACC123456789", BigDecimal.valueOf(100.25));
        when(accountRepository.findByAccountNumber(transactionDto.accountNumber())).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> accountService.deposit(transactionDto));
        assertEquals(String.format("Account with number %s doesn't exist", transactionDto.accountNumber()), ex.getMessage());

        verify(accountRepository, times(1)).findByAccountNumber(transactionDto.accountNumber());
    }

    @Test
    @DisplayName("Withdraw account success: valid input")
    void testWithdraw_Success() {
        TransactionDto transactionDto = new TransactionDto("ACC123456789", BigDecimal.valueOf(10));
        AccountBalanceDto accountBalanceDto = new AccountBalanceDto("ACC123456789", BigDecimal.valueOf(90), null);
        Account account = Account.builder()
                .accountNumber(transactionDto.accountNumber())
                .ownerId(1L)
                .balance(Balance.builder()
                        .actualBalance(BigDecimal.valueOf(100))
                        .authorizedBalance(BigDecimal.ZERO)
                        .build())
                .build();

        when(accountRepository.findByAccountNumber(transactionDto.accountNumber())).thenReturn(Optional.of(account));

        AccountBalanceDto result = accountService.withdraw(1L, transactionDto);

        verify(accountRepository, times(1)).save(account);
        verify(accountRepository, times(1)).findByAccountNumber(transactionDto.accountNumber());

        assertEquals(accountBalanceDto, result);
        assertEquals(BigDecimal.valueOf(90), account.getBalance().getActualBalance());
        assertEquals(BigDecimal.valueOf(10), account.getBalance().getAuthorizedBalance());
    }

    @Test
    @DisplayName("Withdraw account fail: invalid account status")
    void testWithdraw_InvalidAccountStatus_Fail() {
        TransactionDto transactionDto = new TransactionDto("ACC123456789", BigDecimal.valueOf(10));
        Account account = Account.builder()
                .accountNumber(transactionDto.accountNumber())
                .ownerId(1L)
                .status(AccountStatus.FROZEN)
                .build();

        when(accountRepository.findByAccountNumber(transactionDto.accountNumber())).thenReturn(Optional.of(account));

        AccountWithdrawalException ex = assertThrows(AccountWithdrawalException.class, () -> accountService.withdraw(1L, transactionDto));
        assertEquals("Withdrawal from account ACC123456789 is not possible. Status: FROZEN", ex.getMessage());
    }

    @Test
    @DisplayName("Withdraw account fail: not enough funds")
    void testWithdraw_NotEnoughFunds_Fail() {
        TransactionDto transactionDto = new TransactionDto("ACC123456789", BigDecimal.valueOf(100));
        Account account = Account.builder()
                .accountNumber(transactionDto.accountNumber())
                .ownerId(1L)
                .balance(Balance.builder()
                        .actualBalance(BigDecimal.valueOf(10))
                        .build())
                .build();

        when(accountRepository.findByAccountNumber(transactionDto.accountNumber())).thenReturn(Optional.of(account));

        AccountWithdrawalException ex = assertThrows(AccountWithdrawalException.class, () -> accountService.withdraw(1L, transactionDto));
        assertEquals("Not enough funds for the transaction. Available: 10, Transaction: 100", ex.getMessage());
    }

    @Test
    @DisplayName("Approve transaction success: valid input")
    void testApprove_Success() {
        TransactionDto transactionDto = new TransactionDto("ACC123456789", BigDecimal.valueOf(10));
        Account account = Account.builder()
                .accountNumber(transactionDto.accountNumber())
                .ownerId(1L)
                .balance(Balance.builder()
                        .actualBalance(BigDecimal.valueOf(90))
                        .authorizedBalance(BigDecimal.valueOf(20))
                        .build())
                .build();

        when(accountRepository.findByAccountNumber(transactionDto.accountNumber())).thenReturn(Optional.of(account));

        accountService.approve(transactionDto);

        verify(accountRepository, times(1)).save(account);
        verify(accountRepository, times(1)).findByAccountNumber(transactionDto.accountNumber());

        assertEquals(BigDecimal.valueOf(90), account.getBalance().getActualBalance());
        assertEquals(BigDecimal.valueOf(10), account.getBalance().getAuthorizedBalance());
    }

    @Test
    @DisplayName("Cancel transaction success: valid input")
    void testCancel_Success() {
        TransactionDto transactionDto = new TransactionDto("ACC123456789", BigDecimal.valueOf(10));
        Account account = Account.builder()
                .accountNumber(transactionDto.accountNumber())
                .ownerId(1L)
                .balance(Balance.builder()
                        .actualBalance(BigDecimal.valueOf(90))
                        .authorizedBalance(BigDecimal.valueOf(20))
                        .build())
                .build();

        when(accountRepository.findByAccountNumber(transactionDto.accountNumber())).thenReturn(Optional.of(account));

        accountService.cancel(transactionDto);

        verify(accountRepository, times(1)).save(account);
        verify(accountRepository, times(1)).findByAccountNumber(transactionDto.accountNumber());

        assertEquals(BigDecimal.valueOf(100), account.getBalance().getActualBalance());
        assertEquals(BigDecimal.valueOf(10), account.getBalance().getAuthorizedBalance());
    }

    @Test
    @DisplayName("Get account balance success: valid input")
    void testGetAccountBalance_Success() {
        Long ownerId = 1L;
        String accountNumber = "ACC123456789";
        AccountBalanceDto accountBalanceDto = new AccountBalanceDto(accountNumber, BigDecimal.valueOf(100), null);
        BigDecimal expectedBalance = BigDecimal.valueOf(100);
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .ownerId(ownerId)
                .balance(Balance.builder()
                        .actualBalance(expectedBalance)
                        .build())
                .build();

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        AccountBalanceDto result = accountService.getAccountBalance(ownerId, accountNumber);

        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);

        assertEquals(accountBalanceDto, result);
    }
}