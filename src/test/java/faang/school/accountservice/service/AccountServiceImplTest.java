package faang.school.accountservice.service;

import faang.school.accountservice.adapter.AccountRepositoryAdapter;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountOwnerType;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.mapper.AccountMapperImpl;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {
    @InjectMocks
    private AccountServiceImpl accountService;

    @Spy
    private AccountMapper accountMapper = new AccountMapperImpl();
    @Mock
    private AccountRepositoryAdapter accountRepositoryAdapter;

    @Test
    public void testCreateAccountSuccess() {
        AccountDto accountDto = AccountDto.builder()
                .accountNumber("12356789101112")
                .ownerId(BigInteger.valueOf(1))
                .ownerType(AccountOwnerType.USER)
                .type(AccountType.CURRENCY_ACCOUNT)
                .currency(Currency.USD)
                .build();
        Account account = accountMapper.toEntity(accountDto);
        account.setAccountStatus(AccountStatus.ACTIVE);
        when(accountRepositoryAdapter.existsByAccountNumberAndOwnerIdAndOwnerTypeAndType(accountDto.getAccountNumber(),
                accountDto.getOwnerId(), accountDto.getOwnerType(), accountDto.getType())).thenReturn(false);
        accountService.createAccount(accountDto);
        verify(accountRepositoryAdapter, times(1)).save(account);
    }

    @Test
    public void testCreateAccountFailed() {
        AccountDto accountDto = AccountDto.builder()
                .accountNumber("12356789101112")
                .ownerId(BigInteger.valueOf(1))
                .ownerType(AccountOwnerType.USER)
                .type(AccountType.CURRENCY_ACCOUNT)
                .currency(Currency.USD)
                .build();
        Account account = accountMapper.toEntity(accountDto);
        account.setAccountStatus(AccountStatus.ACTIVE);
        when(accountRepositoryAdapter.existsByAccountNumberAndOwnerIdAndOwnerTypeAndType(accountDto.getAccountNumber(),
                accountDto.getOwnerId(), accountDto.getOwnerType(), accountDto.getType())).thenReturn(true);
        Assert.assertThrows(
                DataValidationException.class,
                () -> accountService.createAccount(accountDto));
    }

    @Test
    public void testBlockAccountSuccess() {
        Account account = Account.builder()
                .accountNumber("12356789101112")
                .ownerId(BigInteger.valueOf(1))
                .ownerType(AccountOwnerType.USER)
                .type(AccountType.CURRENCY_ACCOUNT)
                .currency(Currency.USD)
                .accountStatus(AccountStatus.FROZEN)
                .build();
        when(accountRepositoryAdapter.findById(1L)).thenReturn(account);
        accountService.blockAccount(1L);
        verify(accountRepositoryAdapter, times(1)).save(account);
    }

    @Test
    public void testCloseAccountAlready() {
        Account account = Account.builder()
                .accountNumber("12356789101112")
                .ownerId(BigInteger.valueOf(1))
                .ownerType(AccountOwnerType.USER)
                .type(AccountType.CURRENCY_ACCOUNT)
                .currency(Currency.USD)
                .accountStatus(AccountStatus.CLOSED)
                .build();
        when(accountRepositoryAdapter.findById(1L)).thenReturn(account);
        accountService.closeAccount(1L);
        verify(accountRepositoryAdapter, times(1)).save(account);
    }

    @Test
    public void testCloseAccountSuccess() {
        Account account = Account.builder()
                .accountNumber("12356789101112")
                .ownerId(BigInteger.valueOf(1))
                .ownerType(AccountOwnerType.USER)
                .type(AccountType.CURRENCY_ACCOUNT)
                .currency(Currency.USD)
                .build();
        when(accountRepositoryAdapter.findById(1L)).thenReturn(account);
        AccountDto accountDtoExpected = accountMapper.toDto(account);
        accountDtoExpected.setAccountStatus(AccountStatus.CLOSED);
        accountService.closeAccount(1L);
        verify(accountRepositoryAdapter, times(1)).save(account);
    }
}
