package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.AccountDtoToUpdate;
import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Owner;
import faang.school.accountservice.model.enums.AccountStatus;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.OwnerRepository;
import faang.school.accountservice.service.account_number.AccountNumberService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountValidator accountValidator;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private AccountNumberService accountNumberService;

    @InjectMocks
    private AccountServiceImpl accountService;

    private AccountCreateDto accountCreateDto;
    private AccountDto accountDto;
    private AccountDtoToUpdate accountDtoToUpdate;
    private Account account;
    private Owner owner;

    @BeforeEach
    void setUp() {
        accountCreateDto = new AccountCreateDto();
        accountDtoToUpdate = new AccountDtoToUpdate();
        account = new Account();
        owner = new Owner();
        account.setOwner(owner);
        account.setVersion(1L);
        accountDto = new AccountDto();
    }

    @Test
    void testOpen() {
        when(accountMapper.toEntity(accountCreateDto)).thenReturn(account);
        doNothing().when(accountValidator).validateCreate(account);
        when(ownerRepository.findByProjectOrUserIdAndOwnerType(anyLong(), any())).thenReturn(Optional.empty());
        when(ownerRepository.save(any(Owner.class))).thenReturn(owner);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountMapper.toDto(any(Account.class))).thenReturn(accountDto);

        AccountDto result = accountService.open(accountCreateDto);

        assertNotNull(result);
        verify(accountRepository).save(account);
        verify(accountMapper).toDto(account);
        verify(accountValidator).validateCreate(account);
        verify(accountNumberService).getUniqueAccountNumber(any(), eq(accountCreateDto.getAccountType()));
    }

    @Test
    void testUpdate() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        doNothing().when(accountMapper).update(accountDtoToUpdate, account);
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
        doNothing().when(accountValidator).validateBlock(account);

        accountService.block(1L);

        assertEquals(AccountStatus.FROZEN, account.getAccountStatus());
        verify(accountRepository).save(account);
        verify(accountValidator).validateBlock(account);
    }

    @Test
    void testUnblock() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        doNothing().when(accountValidator).validateUnblock(account);

        accountService.unBlock(1L);

        assertEquals(AccountStatus.ACTIVE, account.getAccountStatus());
        verify(accountRepository).save(account);
        verify(accountValidator).validateUnblock(account);
    }

    @Test
    void testClose() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        doNothing().when(accountValidator).validateClose(account);

        accountService.close(1L);

        assertEquals(AccountStatus.CLOSED, account.getAccountStatus());
        assertNotNull(account.getClosedAt());
        verify(accountRepository).save(account);
        verify(accountValidator).validateClose(account);
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
