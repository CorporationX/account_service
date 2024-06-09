package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.OwnerDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Owner;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.OwnerRepository;
import faang.school.accountservice.validation.AccountValidate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @InjectMocks
    private AccountService accountService;
    @Mock
    private BalanceService balanceService;
    @Mock
    private AccountMapper accountMapper;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountValidate accountValidate;
    @Mock
    private OwnerRepository ownerRepository;

    @Test
    public void test_open_InvalidMapping(){
        AccountDto accountDto = getAccountDto();

        when(accountMapper.toEntity(accountDto)).thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> accountService.open(accountDto));
    }

    @Test
    public void test_open_SaveThrowException(){
        AccountDto accountDto = getAccountDto();
        Account account = getAccount();
        Owner owner = account.getOwner();
        account.setAccountStatus(AccountStatus.ACTIVE);

        when(accountMapper.toEntity(accountDto)).thenReturn(account);
        when(ownerRepository.findByAccountIdAndOwnerType(owner.getAccountId(), owner.getOwnerType())).thenReturn(Optional.of(owner));
        when(accountRepository.save(account)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> accountService.open(accountDto));
    }

    @Test
    public void test_open_Successful(){
        AccountDto expected = getAccountDto();
        Account account = getAccount();
        Owner owner = account.getOwner();
        account.setAccountStatus(AccountStatus.ACTIVE);

        when(accountMapper.toEntity(expected)).thenReturn(account);
        when(ownerRepository.findByAccountIdAndOwnerType(owner.getAccountId(), owner.getOwnerType())).thenReturn(Optional.of(owner));
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toDto(account)).thenReturn(expected);
        AccountDto actual = accountService.open(expected);

        Assertions.assertEquals(expected, actual);
        verify(accountMapper, times(1)).toEntity(expected);
        verify(ownerRepository, times(1)).findByAccountIdAndOwnerType(owner.getAccountId(), owner.getOwnerType());
        verify(accountRepository, times(1)).save(account);
        verify(accountMapper, times(1)).toDto(account);
    }

    @Test
    public void test_get_NotFound(){
        long accountId = 1L;

        when(accountRepository.findById(accountId)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class, () -> accountService.get(accountId));
    }

    @Test
    public void test_get_Successful(){
        long accountId = 1L;
        Account account = getAccount();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        Assertions.assertDoesNotThrow(() -> accountService.get(accountId));
    }

    @Test
    public void test_block_SaveThrowException(){
        long accountId = 1L;
        Account account = getAccount();
        account.setAccountStatus(AccountStatus.FROZEN);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenThrow(RuntimeException.class);

        Assertions.assertThrows(RuntimeException.class, () -> accountService.block(accountId));
    }

    @Test
    public void test_block_Successful(){
        long accountId = 1L;
        Account account = getAccount();
        account.setAccountStatus(AccountStatus.FROZEN);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        Assertions.assertDoesNotThrow(() -> accountService.block(accountId));
    }

    @Test
    public void test_unBlock_SaveThrowException(){
        long accountId = 1L;
        Account account = getAccount();
        account.setAccountStatus(AccountStatus.ACTIVE);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenThrow(RuntimeException.class);

        Assertions.assertThrows(RuntimeException.class, () -> accountService.unBlock(accountId));
    }

    @Test
    public void test_unBlock_Successful(){
        long accountId = 1L;
        Account account = getAccount();
        account.setAccountStatus(AccountStatus.ACTIVE);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        Assertions.assertDoesNotThrow(() -> accountService.unBlock(accountId));
    }

    @Test
    public void test_delete_SaveThrowException(){
        long accountId = 1L;
        Account account = getAccount();
        account.setAccountStatus(AccountStatus.CLOSED);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenThrow(RuntimeException.class);

        Assertions.assertThrows(RuntimeException.class, () -> accountService.delete(accountId));
    }

    @Test
    public void test_delete_Successful(){
        long accountId = 1L;
        Account account = getAccount();
        account.setAccountStatus(AccountStatus.CLOSED);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        Assertions.assertDoesNotThrow(() -> accountService.delete(accountId));
    }
    private AccountDto getAccountDto(){
        return AccountDto.builder()
                .owner(getOwnerDto())
                .number("123456789012345")
                .currency(Currency.USD)
                .accountType(AccountType.INDIVIDUAL)
                .build();
    }

    private OwnerDto getOwnerDto(){
        return OwnerDto.builder()
                .accountId(1L)
                .id(1)
                .ownerType(OwnerType.USER)
                .build();
    }

    private Owner getOwner(){
        return Owner.builder()
                .accountId(1L)
                .id(1)
                .ownerType(OwnerType.USER)
                .build();
    }

    private Account getAccount(){
        return Account.builder()
                .owner(getOwner())
                .number("123456789012345")
                .currency(Currency.USD)
                .accountType(AccountType.INDIVIDUAL)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
    }
}
