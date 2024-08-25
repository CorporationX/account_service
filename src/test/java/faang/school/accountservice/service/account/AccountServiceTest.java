package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.model.AccountStatus;
import faang.school.accountservice.model.OwnerType;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.EnumConverter;
import faang.school.accountservice.validator.account.AccountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountMapper accountMapper;
    @Mock
    private AccountValidator accountValidator;
    @Mock
    private EnumConverter enumConverter;
    @InjectMocks
    private AccountService accountService;
    private final long ownerId = 1L;
    private final String owner = "USER";
    private final String number = "1234567890123";
    private final OwnerType newOwner = OwnerType.USER;
    private Account account;
    private AccountDto accountDto;

    @BeforeEach
    void init() {
        account = Account.builder()
                .id(1L)
                .number("1234567890123")
                .ownerId(1L)
                .owner(OwnerType.USER)
                .accountStatus(AccountStatus.VALID)
                .build();

        accountDto = AccountDto.builder()
                .ownerId(1L)
                .owner(OwnerType.USER)
                .number("1234567890123")
                .build();
    }

    @Test
    @DisplayName("getAllAccountsCheckEnumAndTransformationException")
    void testCheckEnumAndTransformationException() {
        when(enumConverter.checkEnumAndTransformation(anyString(), eq(OwnerType.class)))
                .thenThrow(new RuntimeException("ошибка"));
        Exception exception = assertThrows(RuntimeException.class, () ->
                accountService.getAllAccounts(ownerId, owner));

        assertEquals("ошибка", exception.getMessage());
    }

    @Test
    @DisplayName("getAllAccountsFindAllByOwnerIdAndOwnerException")
    void testFindAllByOwnerIdAndOwnerException() {
        when(enumConverter.checkEnumAndTransformation(anyString(), eq(OwnerType.class)))
                .thenReturn(newOwner);
        when(accountRepository.findAllByOwnerIdAndOwner(anyLong(), any(OwnerType.class)))
                .thenThrow(new RuntimeException("ошибка"));
        Exception exception = assertThrows(RuntimeException.class, () ->
                accountService.getAllAccounts(ownerId, owner));

        assertEquals("ошибка", exception.getMessage());
    }

    @Test
    @DisplayName("getAllAccountsAccountMapperValid")
    void testAccountMapperValid() {
        List<Account> accountList = List.of(account);

        when(enumConverter.checkEnumAndTransformation(anyString(), eq(OwnerType.class)))
                .thenReturn(newOwner);
        when(accountRepository.findAllByOwnerIdAndOwner(anyLong(), any(OwnerType.class)))
                .thenReturn(accountList);
        when(accountMapper.toDto(any(Account.class)))
                .thenReturn(new AccountDto());

        accountService.getAllAccounts(ownerId, owner);

        verify(accountMapper, times(1)).toDto(any(Account.class));
    }

    @Test
    @DisplayName("getAccountFindAccountAndValidNull")
    void testFindAccountAndValidNull() {
        when(accountRepository.findByNumber(anyString()))
                .thenReturn(null);
        assertThrows(NullPointerException.class, () ->
                accountService.getAccount(number));
    }

    @Test
    @DisplayName("getAccountToDtoValid")
    void testGetAccountToDtoValid() {
        when(accountRepository.findByNumber(anyString()))
                .thenReturn(Optional.ofNullable(account));
        when(accountMapper.toDto(any(Account.class)))
                .thenReturn(new AccountDto());

        accountService.getAccount(number);

        verify(accountMapper, times(1))
                .toDto(any(Account.class));
    }

    @Test
    @DisplayName("createAccountSaveException")
    void testCreateAccountSaveException() {
        doNothing().when(accountValidator).checkExistenceOfTheNumber(any(AccountDto.class));
        when(accountMapper.toEntity(any(AccountDto.class)))
                .thenReturn(account);
        when(accountRepository.save(any(Account.class)))
                .thenThrow(new RuntimeException("ошибка"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                accountService.createAccount(accountDto));

        assertEquals("ошибка", exception.getMessage());
    }

    @Test
    @DisplayName("createAccountToDtoValid")
    void testCreateAccountToDtoValid() {
        doNothing().when(accountValidator).checkExistenceOfTheNumber(any(AccountDto.class));
        when(accountMapper.toEntity(any(AccountDto.class)))
                .thenReturn(account);
        when(accountRepository.save(any(Account.class)))
                .thenReturn(new Account());
        when(accountMapper.toDto(any(Account.class)))
                .thenReturn(new AccountDto());

        accountService.createAccount(accountDto);

        verify(accountMapper, times(1)).toDto(any(Account.class));
    }

    @Test
    @DisplayName("updateStatusAccountFindAccountAndValidNull")
    void testUpdateStatusAccountFindAccountAndValidNull() {
        when(accountRepository.findByNumber(anyString()))
                .thenReturn(null);
        assertThrows(NullPointerException.class, () ->
                accountService.getAccount(number));
    }

    @Test
    @DisplayName("updateStatusAccountCLOSEDException")
    void testUpdateStatusAccountCLOSEDException() {
        String status = "FROZEN";
        when(accountRepository.findByNumber(number))
                .thenReturn(Optional.ofNullable(account));
        account.setAccountStatus(AccountStatus.CLOSED);
        assertThrows(IllegalArgumentException.class, () ->
                accountService.updateStatusAccount(number, status));
    }

    @Test
    @DisplayName("updateStatusAccountNewStatusClosed")
    void testUpdateStatusAccountNewStatusClosed() {
        String status = "CLOSED";
        AccountStatus newStatus = AccountStatus.CLOSED;

        when(accountRepository.findByNumber(number))
                .thenReturn(Optional.ofNullable(account));
        when(enumConverter.checkEnumAndTransformation(anyString(), eq(AccountStatus.class)))
                .thenReturn(newStatus);
        when(accountRepository.save(any(Account.class)))
                .thenReturn(new Account());

        accountService.updateStatusAccount(number, status);

        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("updateStatusAccountNewStatusFrozen")
    void testUpdateStatusAccountNewStatusFrozen() {
        String status = "FROZEN";
        AccountStatus newStatus = AccountStatus.FROZEN;

        when(accountRepository.findByNumber(number))
                .thenReturn(Optional.ofNullable(account));
        when(enumConverter.checkEnumAndTransformation(anyString(), eq(AccountStatus.class)))
                .thenReturn(newStatus);
        when(accountRepository.save(any(Account.class)))
                .thenReturn(new Account());
        when(accountMapper.toDto(any(Account.class)))
                .thenReturn(new AccountDto());

        accountService.updateStatusAccount(number, status);

        verify(accountMapper, times(1))
                .toDto(any(Account.class));
    }
}