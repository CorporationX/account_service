/*
package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.enums.Status;
import faang.school.accountservice.mapper.AccountMapperImpl;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Owner;
import faang.school.accountservice.repository.AccountRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Spy
    private AccountMapperImpl accountMapper;

    @InjectMocks
    private AccountService accountService;

    private Owner owner;
    private Account account;
    private Account secondAccount;
    private AccountDto accountDto;

    @BeforeEach
    public void init() {
        owner = Owner.builder()
                .id(1L)
                .owner_id(1L)
                .ownerType(OwnerType.USER)
                .build();
        account = Account.builder()
                .id(1L)
                .currency(Currency.USD)
                .number("1010202030304040")
                .owner(owner)
                .status(Status.SUSPENDED)
                .type(AccountType.CREDIT)
                .build();
        secondAccount = Account.builder()
                .id(2L)
                .currency(Currency.CNY)
                .number("9999888811112222")
                .owner(owner)
                .status(Status.BLOCKED)
                .type(AccountType.CURRENCY)
                .build();
        accountDto = AccountDto.builder()
                .id(1L)
                .currency(Currency.USD)
                .number("1010202030304040")
                .status(Status.ACTIVE)
                .type(AccountType.CREDIT)
                .ownerId(1L)
                .build();
    }

    @Test
    public void getAccountSuccess() {
        Mockito.when(accountRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(account));
        Assertions.assertEquals(account, accountService.getAccount(1L));
    }

    @Test
    public void getAccountFailed() {
        Mockito.when(accountRepository.findById(2L)).thenReturn(java.util.Optional.ofNullable(secondAccount));
        Assertions.assertNotEquals(account, accountService.getAccount(2L));
    }

    @Test
    public void openAccountSuccess() {
        Mockito.lenient().when(accountRepository.save(Mockito.any(Account.class))).thenReturn(account);
        AccountDto acc = accountService.open(accountDto);
        Assertions.assertNotNull(acc);
        Assertions.assertEquals(account.getNumber(), acc.getNumber());
    }

    @Test
    public void openAccountFailed() {
        Mockito.lenient().when(accountRepository.save(Mockito.any(Account.class))).thenReturn(secondAccount);
        AccountDto acc = accountService.open(accountDto);
        Assertions.assertNotNull(acc);
        Assertions.assertNotEquals(account.getNumber(), acc.getNumber());
    }

    @Test
    public void blockAccountSuccess() {
        Mockito.when(accountRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(account));
        Status status = Status.BLOCKED;
        AccountDto acc = accountService.block(1L, status);
        Assertions.assertNotNull(acc);
        Assertions.assertEquals(acc.getStatus(), status);
    }

    @Test
    public void blockAccountFailed() {
        Mockito.when(accountRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(account));
        Status status = Status.BLOCKED;
        AccountDto acc = accountService.block(1L, status);
        Assertions.assertNotNull(acc);
        Assertions.assertNotEquals(acc.getStatus(), Status.ACTIVE);
    }

    @Test
    public void suspendedAccountSuccess() {
        Mockito.when(accountRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(account));
        Status status = Status.SUSPENDED;
        AccountDto acc = accountService.block(1L, status);
        Assertions.assertNotNull(acc);
        Assertions.assertEquals(acc.getStatus(), status);
    }

    @Test
    public void suspendedAccountFailed() {
        Mockito.when(accountRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(account));
        Status status = Status.SUSPENDED;
        AccountDto acc = accountService.block(1L, status);
        Assertions.assertNotNull(acc);
        Assertions.assertNotEquals(acc.getStatus(), Status.ACTIVE);
    }

    @Test
    public void blockStatusValidateFailed() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> accountService.blockStatusValidate(Status.ACTIVE));
        Assert.assertThrows(IllegalArgumentException.class,
                () -> accountService.blockStatusValidate(Status.CLOSED));
    }

    @Test
    public void blockStatusValidateSuccess() {
        assertDoesNotThrow(() -> accountService.blockStatusValidate(Status.BLOCKED));
        assertDoesNotThrow(() -> accountService.blockStatusValidate(Status.SUSPENDED));
    }
}
 */
