package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.exception.ForbiddenException;
import faang.school.accountservice.mapper.AccountMapperImpl;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.model.Owner;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.OwnerRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Spy
    private AccountMapperImpl accountMapper;

    private AccountServiceImpl accountService;

    @Captor
    private ArgumentCaptor<Account> accountArgumentCaptor;

    private static Account account;

    @BeforeAll
    public static void init() {
        account = Account.builder()
                .accountNumber("111222333444555")
                .accountStatus(AccountStatus.ACTIVE)
                .balance(new BigDecimal(1000))
                .owner(new Owner(1L, 2L, OwnerType.PROJECT, null))
                .build();
    }

    @BeforeEach
    void setUp() {
        OperationValidator realOperationValidator = new OperationValidator(accountRepository);
        accountService = new AccountServiceImpl(
                accountRepository,
                accountMapper,
                ownerRepository,
                realOperationValidator
        );
    }

    @Test
    public void testGeyByIdException() {
        assertThrows(EntityNotFoundException.class, () -> accountService.getById(anyLong()));
    }

    @Test
    public void testGetByInvalidNumber() {
        when(accountRepository.findByAccountNumber(anyString()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> accountService.getByAccountNumber(anyString()));
    }

    @Test
    public void testGetByNumber() {
        when(accountRepository.findByAccountNumber(anyString()))
                .thenReturn(Optional.of(account));

        AccountDto accountTest = accountService.getByAccountNumber(anyString());

        assertEquals(accountTest.accountNumber(), account.getAccountNumber());
        assertEquals(accountTest.balance(), account.getBalance());
        assertEquals(accountTest.id(), account.getId());
    }

    @Test
    public void testGetByOwner() {
        when(accountRepository.findByPersonIdAndOwnerType(anyLong(), any(OwnerType.class)))
                .thenReturn(List.of(account, new Account()));

        List<AccountDto> accountTest = accountService.getAccountsByOwner(account.getOwner().getPersonId(),
                account.getOwner().getOwnerType());

        assertEquals(accountTest.size(), 2);
    }

    @Test
    public void testOpenAccount() {
        CreateAccountDto createAccountTest = new CreateAccountDto(
                new BigDecimal(1000),
                1L,
                OwnerType.PROJECT,
                AccountType.CREDIT,
                Currency.EUR);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        accountService.openAccount(createAccountTest);

        verify(accountRepository).save(accountArgumentCaptor.capture());
        Account accountTest = accountArgumentCaptor.getValue();
        assertEquals(createAccountTest.balance(), accountTest.getBalance());
        assertEquals(createAccountTest.accountType(), accountTest.getAccountType());
    }

    @Test
    public void testBlockByInvalidNumber() {
        when(accountRepository.findByAccountNumber(anyString()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> accountService.blockAccount(anyString()));
    }

    @Test
    public void testBlockWithInvalidStatus() {
        when(accountRepository.findByAccountNumber(anyString()))
                .thenReturn(Optional.of(account));
        account.setAccountStatus(AccountStatus.FROZEN);

        assertThrows(ForbiddenException.class, () -> accountService.blockAccount(anyString()));
    }

    @Test
    public void testBlockAccount() {
        account.setAccountStatus(AccountStatus.ACTIVE);
        when(accountRepository.findByAccountNumber(anyString()))
                .thenReturn(Optional.of(account));
        AccountDto accountTest = accountService.blockAccount(anyString());

        assertEquals(accountTest.accountStatus(), AccountStatus.FROZEN);
    }

    @Test
    public void testUnblockWithInvalidNumber() {
        when(accountRepository.findByAccountNumber(anyString()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> accountService.unblockAccount(anyString()));
    }

    @Test
    public void testUnblockWithInvalidStatus() {
        account.setAccountStatus(AccountStatus.ACTIVE);
        when(accountRepository.findByAccountNumber(anyString()))
                .thenReturn(Optional.of(account));

        assertThrows(ForbiddenException.class, () -> accountService.unblockAccount(anyString()));
    }

    @Test
    public void testUnblockAccount() {
        account.setAccountStatus(AccountStatus.FROZEN);
        when(accountRepository.findByAccountNumber(anyString()))
                .thenReturn(Optional.of(account));
        AccountDto accountTest = accountService.unblockAccount(anyString());

        assertEquals(accountTest.accountStatus(), AccountStatus.ACTIVE);
    }

    @Test
    public void testCloseAccountWithInvalidNumber() {
        when(accountRepository.findByAccountNumber(anyString()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> accountService.closeAccount(anyString()));
    }

    @Test
    public void testCloseAccountWithInvalidStatus() {
        account.setAccountStatus(AccountStatus.CLOSED);
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));

        assertThrows(ForbiddenException.class, () -> accountService.closeAccount(anyString()));
    }

    @Test
    public void testCloseAccountNonZeroBalance() {
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setBalance(new BigDecimal(1000));
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));

        assertThrows(ForbiddenException.class, () -> accountService.closeAccount(anyString()));
    }

    @Test
    public void testCloseAccount() {
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setBalance(new BigDecimal(0));
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));
        AccountDto accountTest = accountService.closeAccount(anyString());

        assertEquals(accountTest.accountStatus(), AccountStatus.CLOSED);
    }

    @Test
    public void testWithdrawWithInvalidNumber() {
        when(accountRepository.findByAccountNumber(anyString()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> accountService.withdraw(anyString(), new BigDecimal(100)));
    }

    @Test
    public void testWithdrawWithLowBalance() {
        account.setBalance(new BigDecimal(100));
        account.setAccountStatus(AccountStatus.ACTIVE);
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));

        assertThrows(ForbiddenException.class,
                () -> accountService.withdraw(anyString(), new BigDecimal(1000)));
    }

    @Test
    public void testWithdrawWithNonActiveStatus() {
        account.setBalance(new BigDecimal(1000));
        account.setAccountStatus(AccountStatus.FROZEN);
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));

        assertThrows(ForbiddenException.class,
                () -> accountService.withdraw(anyString(), new BigDecimal(100)));
    }

    @Test
    public void testWithdraw() {
        account.setBalance(new BigDecimal(1000));
        account.setAccountStatus(AccountStatus.ACTIVE);
        BigDecimal amountTest = new BigDecimal(100);
        BigDecimal expectedBalance = account.getBalance().subtract(amountTest);
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));

        AccountDto accountTest = accountService.withdraw(anyString(), amountTest);

        assertEquals(expectedBalance, accountTest.balance());
    }

    @Test
    public void testDepositWithInvalidNumber() {
        when(accountRepository.findByAccountNumber(anyString()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> accountService.deposit(anyString(), new BigDecimal(100)));
    }

    @Test
    public void testDepositWithNonActiveStatus() {
        account.setAccountStatus(AccountStatus.FROZEN);
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));

        assertThrows(ForbiddenException.class,
                () -> accountService.deposit(anyString(), new BigDecimal(100)));
    }

    @Test
    public void testDeposit() {
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setBalance(new BigDecimal(1000));
        BigDecimal amountTest = new BigDecimal(100);
        BigDecimal expectedBalance = account.getBalance().add(amountTest);
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));

        AccountDto accountTest = accountService.deposit(anyString(), amountTest);

        assertEquals(expectedBalance, accountTest.balance());
    }

    @Test
    public void testGetBalanceWithInvalidNumber() {
        when(accountRepository.findByAccountNumber(anyString()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> accountService.getBalance(anyString()));
    }

    @Test
    public void testGetBalanceWithClosedStatus() {
        account.setAccountStatus(AccountStatus.CLOSED);
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));

        assertThrows(ForbiddenException.class, () -> accountService.getBalance(anyString()));
    }

    @Test
    public void testGetBalance() {
        account.setAccountStatus(AccountStatus.ACTIVE);
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));
        assertEquals(account.getBalance(), accountService.getBalance(anyString()));
    }

}