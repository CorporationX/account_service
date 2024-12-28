package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountBalanceDto;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.BalanceChangeDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.dto.TransactionDto;
import faang.school.accountservice.dto.TransactionRequestDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.Transaction;
import faang.school.accountservice.enums.AccountOwnerType;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.TransactionStatus;
import faang.school.accountservice.enums.TransactionType;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private TransactionService transactionService;

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
    @DisplayName("Delete account success: valid input")
    void testDeleteAccount_Success() {
        String accountNumber = "ACC0123456789";
        Long accountId = 1L;
        Long ownerId = 10L;
        Account account = Account.builder().id(accountId).accountNumber(accountNumber).status(AccountStatus.ACTIVE).ownerId(ownerId).build();

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        accountService.deleteAccount(accountNumber, ownerId);

        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
        verify(accountRepository, times(1)).deleteById(account.getId());
    }

    @Test
    @DisplayName("Deposit account success: valid input")
    void testDeposit_Success() {
        TransactionRequestDto transactionRequestDto = new TransactionRequestDto("ACC123456789", BigDecimal.valueOf(100.25));
        BalanceChangeDto balanceChangeDto = new BalanceChangeDto(null, TransactionType.DEPOSIT, BigDecimal.valueOf(100.25), null, BigDecimal.valueOf(110.25));
        Account account = Account.builder()
                .accountNumber(transactionRequestDto.accountNumber())
                .balance(Balance.builder()
                        .actualBalance(BigDecimal.valueOf(10))
                        .build())
                .transactions(new ArrayList<>())
                .build();
        when(accountRepository.findByAccountNumber(transactionRequestDto.accountNumber())).thenReturn(Optional.of(account));

        BalanceChangeDto result = accountService.deposit(transactionRequestDto);

        verify(accountRepository, times(1)).save(account);
        verify(accountRepository, times(1)).findByAccountNumber(transactionRequestDto.accountNumber());

        assertEquals(balanceChangeDto, result);
        assertEquals(BigDecimal.valueOf(110.25), account.getBalance().getActualBalance());
    }

    @Test
    @DisplayName("Deposit account fail: account not found")
    void testDeposit_AccountNotFound_Fail() {
        TransactionRequestDto transactionRequestDto = new TransactionRequestDto("ACC123456789", BigDecimal.valueOf(100.25));
        when(accountRepository.findByAccountNumber(transactionRequestDto.accountNumber())).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> accountService.deposit(transactionRequestDto));
        assertEquals(String.format("Account with number %s doesn't exist", transactionRequestDto.accountNumber()), ex.getMessage());

        verify(accountRepository, times(1)).findByAccountNumber(transactionRequestDto.accountNumber());
    }

    @Test
    @DisplayName("Withdraw account success: valid input")
    void testWithdraw_Success() {
        TransactionRequestDto transactionRequestDto = new TransactionRequestDto("ACC123456789", BigDecimal.valueOf(10));
        BalanceChangeDto balanceChangeDto = new BalanceChangeDto(null, TransactionType.WITHDRAWAL, BigDecimal.valueOf(10), null, BigDecimal.valueOf(90));
        Account account = Account.builder()
                .accountNumber(transactionRequestDto.accountNumber())
                .ownerId(1L)
                .balance(Balance.builder()
                        .actualBalance(BigDecimal.valueOf(100))
                        .authorizedBalance(BigDecimal.ZERO)
                        .build())
                .transactions(new ArrayList<>())
                .build();

        when(accountRepository.findByAccountNumber(transactionRequestDto.accountNumber())).thenReturn(Optional.of(account));

        BalanceChangeDto result = accountService.withdraw(1L, transactionRequestDto);

        verify(accountRepository, times(1)).save(account);
        verify(accountRepository, times(1)).findByAccountNumber(transactionRequestDto.accountNumber());

        assertEquals(balanceChangeDto, result);
        assertEquals(BigDecimal.valueOf(90), account.getBalance().getActualBalance());
        assertEquals(BigDecimal.valueOf(10), account.getBalance().getAuthorizedBalance());
    }

    @Test
    @DisplayName("Withdraw account fail: invalid account status")
    void testWithdraw_InvalidAccountStatus_Fail() {
        TransactionRequestDto transactionRequestDto = new TransactionRequestDto("ACC123456789", BigDecimal.valueOf(10));
        Account account = Account.builder()
                .accountNumber(transactionRequestDto.accountNumber())
                .ownerId(1L)
                .status(AccountStatus.FROZEN)
                .build();

        when(accountRepository.findByAccountNumber(transactionRequestDto.accountNumber())).thenReturn(Optional.of(account));

        AccountWithdrawalException ex = assertThrows(AccountWithdrawalException.class, () -> accountService.withdraw(1L, transactionRequestDto));
        assertEquals("Withdrawal from account ACC123456789 is not possible. Status: FROZEN", ex.getMessage());
    }

    @Test
    @DisplayName("Withdraw account fail: not enough funds")
    void testWithdraw_NotEnoughFunds_Fail() {
        TransactionRequestDto transactionRequestDto = new TransactionRequestDto("ACC123456789", BigDecimal.valueOf(100));
        Account account = Account.builder()
                .accountNumber(transactionRequestDto.accountNumber())
                .ownerId(1L)
                .balance(Balance.builder()
                        .actualBalance(BigDecimal.valueOf(10))
                        .build())
                .build();

        when(accountRepository.findByAccountNumber(transactionRequestDto.accountNumber())).thenReturn(Optional.of(account));

        AccountWithdrawalException ex = assertThrows(AccountWithdrawalException.class, () -> accountService.withdraw(1L, transactionRequestDto));
        assertEquals("Not enough funds for the transaction. Available: 10, Transaction: 100", ex.getMessage());
    }

    @Test
    @DisplayName("Approve transaction success: valid input")
    void testApprove_Success() {
        Long transactionId = 1L;
        TransactionRequestDto transactionRequestDto = new TransactionRequestDto("ACC123456789", BigDecimal.valueOf(10));
        Account account = Account.builder()
                .accountNumber(transactionRequestDto.accountNumber())
                .ownerId(1L)
                .balance(Balance.builder()
                        .actualBalance(BigDecimal.valueOf(90))
                        .authorizedBalance(BigDecimal.valueOf(20))
                        .build())
                .build();

        when(accountRepository.findByAccountNumber(transactionRequestDto.accountNumber())).thenReturn(Optional.of(account));

        accountService.approve(transactionRequestDto, transactionId);

        verify(transactionService, times(1)).validateTransactionExists(transactionId, transactionRequestDto.amount());
        verify(transactionService, times(1)).approveTransaction(transactionId);
        verify(accountRepository, times(1)).save(account);
        verify(accountRepository, times(1)).findByAccountNumber(transactionRequestDto.accountNumber());

        assertEquals(BigDecimal.valueOf(90), account.getBalance().getActualBalance());
        assertEquals(BigDecimal.valueOf(10), account.getBalance().getAuthorizedBalance());
    }

    @Test
    @DisplayName("Reject transaction success: valid input")
    void testReject_Success() {
        Long transactionId = 1L;
        TransactionRequestDto transactionRequestDto = new TransactionRequestDto("ACC123456789", BigDecimal.valueOf(10));
        Account account = Account.builder()
                .accountNumber(transactionRequestDto.accountNumber())
                .ownerId(1L)
                .balance(Balance.builder()
                        .actualBalance(BigDecimal.valueOf(90))
                        .authorizedBalance(BigDecimal.valueOf(20))
                        .build())
                .build();

        when(accountRepository.findByAccountNumber(transactionRequestDto.accountNumber())).thenReturn(Optional.of(account));

        accountService.reject(transactionRequestDto, transactionId);

        verify(transactionService, times(1)).validateTransactionExists(transactionId, transactionRequestDto.amount());
        verify(transactionService, times(1)).rejectTransaction(transactionId);
        verify(accountRepository, times(1)).save(account);
        verify(accountRepository, times(1)).findByAccountNumber(transactionRequestDto.accountNumber());

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

    @Test
    @DisplayName("Get all transactions success: valid input")
    void testGetTransactions_Success() {
        Long ownerId = 1L;
        String accountNumber = "ACC123456789";
        List<Transaction> transactions = List.of(Transaction.builder()
                .transactionAmount(BigDecimal.valueOf(100))
                .transactionType(TransactionType.DEPOSIT)
                .transactionStatus(TransactionStatus.PENDING)
                .updatedAt(LocalDateTime.of(2024, 1, 1, 0, 0, 0))
                .build());
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .ownerId(ownerId)
                .transactions(transactions)
                .build();

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        List<TransactionDto> expected = account.getTransactions().stream()
                .map(transaction -> accountMapper.toDto(transaction))
                .toList();

        List<TransactionDto> result = accountService.getTransactions(ownerId, accountNumber);

        assertEquals(expected, result);
    }
}