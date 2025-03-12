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

    private static final String ACCOUNT_NUMBER = "12356789101112";
    private static final BigInteger OWNER_ID = BigInteger.valueOf(1);
    private static final AccountOwnerType OWNER_TYPE = AccountOwnerType.USER;
    private static final AccountType ACCOUNT_TYPE = AccountType.CURRENCY_ACCOUNT;
    private static final Currency CURRENCY = Currency.USD;
    private static final Long ACCOUNT_ID = 1L;

    @Test
    public void testCreateAccountSuccess() {
        AccountDto accountDto = AccountDto.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .ownerId(OWNER_ID)
                .ownerType(OWNER_TYPE)
                .type(ACCOUNT_TYPE)
                .currency(CURRENCY)
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
                .accountNumber(ACCOUNT_NUMBER)
                .ownerId(OWNER_ID)
                .ownerType(OWNER_TYPE)
                .type(ACCOUNT_TYPE)
                .currency(CURRENCY)
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
    public void testBlockAccountAlready() {
        Account account = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .ownerId(OWNER_ID)
                .ownerType(OWNER_TYPE)
                .type(ACCOUNT_TYPE)
                .currency(CURRENCY)
                .accountStatus(AccountStatus.FROZEN)
                .build();
        when(accountRepositoryAdapter.findById(ACCOUNT_ID)).thenReturn(account);
        accountService.blockAccount(ACCOUNT_ID);
        verify(accountRepositoryAdapter, times(1)).findById(ACCOUNT_ID);
        verify(accountRepositoryAdapter, never()).save(any());
    }

    @Test
    public void testBlockAccountSuccess() {
        Account account = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .ownerId(OWNER_ID)
                .ownerType(OWNER_TYPE)
                .type(ACCOUNT_TYPE)
                .currency(CURRENCY)
                .build();
        when(accountRepositoryAdapter.findById(ACCOUNT_ID)).thenReturn(account);
        AccountDto accountDtoExpected = accountMapper.toDto(account);
        accountDtoExpected.setAccountStatus(AccountStatus.FROZEN);
        accountService.blockAccount(ACCOUNT_ID);
        verify(accountRepositoryAdapter, times(1)).save(account);
    }

    @Test
    public void testCloseAccountAlready() {
        Account account = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .ownerId(OWNER_ID)
                .ownerType(OWNER_TYPE)
                .type(ACCOUNT_TYPE)
                .currency(CURRENCY)
                .accountStatus(AccountStatus.CLOSED)
                .build();
        when(accountRepositoryAdapter.findById(ACCOUNT_ID)).thenReturn(account);
        accountService.closeAccount(ACCOUNT_ID);
        verify(accountRepositoryAdapter, times(1)).findById(1L);
        verify(accountRepositoryAdapter, never()).save(any());
    }

    @Test
    public void testCloseAccountSuccess() {
        Account account = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .ownerId(OWNER_ID)
                .ownerType(OWNER_TYPE)
                .type(ACCOUNT_TYPE)
                .currency(CURRENCY)
                .build();
        when(accountRepositoryAdapter.findById(ACCOUNT_ID)).thenReturn(account);
        AccountDto accountDtoExpected = accountMapper.toDto(account);
        accountDtoExpected.setAccountStatus(AccountStatus.CLOSED);
        accountService.closeAccount(ACCOUNT_ID);
        verify(accountRepositoryAdapter, times(1)).save(account);
    }
}
