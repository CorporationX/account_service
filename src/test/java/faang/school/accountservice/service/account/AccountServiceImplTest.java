package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.AccountDtoToUpdate;
import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Owner;
import faang.school.accountservice.model.enums.AccountStatus;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.OwnerRepository;
import faang.school.accountservice.service.balance.BalanceService;
import faang.school.accountservice.validator.AccountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private BalanceService balanceService;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountValidator accountValidator;

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private AccountDto accountDto;
    private AccountDtoToUpdate accountDtoToUpdate;
    private Account account;
    private Owner owner;

    @BeforeEach
    void setUp() {
        accountDto = new AccountDto();
        accountDtoToUpdate = new AccountDtoToUpdate();
        account = new Account();
        owner = new Owner();
        account.setOwner(owner);
    }

    @Test
    void testOpen() {
        when(accountMapper.toEntity(accountDto)).thenReturn(account);
        when(ownerRepository.save(any(Owner.class))).thenReturn(owner);
        when(accountMapper.toDto(any(Account.class))).thenReturn(accountDto);

        AccountDto result = accountService.open(accountDto);

        assertNotNull(result);
        verify(accountRepository).save(account);
        verify(balanceService).createBalance(account);
        verify(accountMapper).toDto(account);
    }


    @Test
    void testUpdate() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        when(accountMapper.toDto(any())).thenReturn(accountDto);

        AccountDto result = accountService.update(1L, accountDtoToUpdate);

        assertNotNull(result);
        verify(accountRepository).save(account);
        verify(accountMapper).update(accountDtoToUpdate, account);
        verify(accountMapper).toDto(account);
    }

    @Test
    void testGet() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        when(accountMapper.toDto(any())).thenReturn(accountDto);

        AccountDto result = accountService.get(1L);

        assertNotNull(result);
        verify(accountMapper).toDto(account);
    }

    @Test
    void testBlock() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));

        accountService.block(1L);

        assertEquals(AccountStatus.FROZEN, account.getAccountStatus());
        verify(accountRepository).save(account);
    }

    @Test
    void testUnblock() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));

        accountService.unBlock(1L);

        assertEquals(AccountStatus.ACTIVE, account.getAccountStatus());
        verify(accountRepository).save(account);
    }

    @Test
    void testClose() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));

        accountService.close(1L);

        assertEquals(AccountStatus.CLOSED, account.getAccountStatus());
        assertNotNull(account.getClosedAt());
        verify(accountRepository).save(account);
    }

    @Test
    void testFindAccountById() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));

        Account result = accountService.findAccountById(1L);

        assertNotNull(result);
        assertEquals(account, result);
    }

    @Test
    void testFindAccountById_NotFound() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountService.findAccountById(1L));
    }
}
