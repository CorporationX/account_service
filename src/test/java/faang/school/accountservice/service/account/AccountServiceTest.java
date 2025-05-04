package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountViewDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.AccountAlreadyClosedException;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.AccountOperationConflictException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.balance.BalanceService;
import faang.school.accountservice.validation.AccountValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для AccountService")
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private AccountValidator accountValidator;

    @Mock
    private AccountHelper accountHelper;

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private AccountService accountService;

    @Nested
    @DisplayName("Тесты для метода getAccount")
    class GetAccountTests {

        @Test
        @DisplayName("Успешное получение счета по ID")
        void givenValidId_WhenGetAccount_ThenReturnsAccountViewDto() {
            Long accountId = 1L;
            Account account = Account.builder()
                    .id(accountId)
                    .accountNumber("1234567890123456")
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();
            AccountViewDto accountViewDto = AccountViewDto.builder()
                    .accountNumber("1234567890123456")
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();

            when(accountHelper.getAccountById(accountId)).thenReturn(account);
            when(accountMapper.toViewDto(account)).thenReturn(accountViewDto);

            AccountViewDto result = accountService.getAccount(accountId);

            assertEquals(accountViewDto, result);
            verify(accountHelper).getAccountById(accountId);
            verify(accountMapper).toViewDto(account);
        }

        @Test
        @DisplayName("Получение счета с несуществующим ID")
        void givenInvalidId_WhenGetAccount_ThenThrowsNotFoundException() {
            Long accountId = 999L;
            when(accountHelper.getAccountById(accountId)).thenThrow(new AccountNotFoundException("Account not found"));

            assertThrows(AccountNotFoundException.class, () -> accountService.getAccount(accountId));
            verify(accountHelper).getAccountById(accountId);
        }
    }

    @Nested
    @DisplayName("Тесты для метода getAccountsByOwner")
    class GetAccountsByOwnerTests {

        @Test
        @DisplayName("Успешное получение счетов по владельцу")
        void givenValidOwnerParams_WhenGetAccountsByOwner_ThenReturnsPage() {
            OwnerType ownerType = OwnerType.USER;
            Long ownerId = 1L;
            PageRequest pageable = PageRequest.of(0, 20);
            Account account = Account.builder()
                    .id(1L)
                    .accountNumber("1234567890123456")
                    .ownerType(ownerType)
                    .ownerId(ownerId)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();
            AccountViewDto accountViewDto = AccountViewDto.builder()
                    .accountNumber("1234567890123456")
                    .ownerType(ownerType)
                    .ownerId(ownerId)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();
            Page<Account> page = new PageImpl<>(Collections.singletonList(account), pageable, 1);

            when(accountRepository.findByOwnerTypeAndOwnerId(ownerType, ownerId, pageable)).thenReturn(page);
            when(accountMapper.toViewDto(account)).thenReturn(accountViewDto);

            Page<AccountViewDto> result = accountService.getAccountsByOwner(ownerType, ownerId, pageable);

            assertEquals(1, result.getTotalElements());
            assertEquals(accountViewDto, result.getContent().get(0));
            verify(accountRepository).findByOwnerTypeAndOwnerId(ownerType, ownerId, pageable);
            verify(accountMapper).toViewDto(account);
        }
    }

    @Nested
    @DisplayName("Тесты для метода openAccount")
    class OpenAccountTests {

        @Test
        @DisplayName("Успешное создание нового счета")
        void givenValidAccountData_WhenOpenAccount_ThenReturnsAccountViewDto() {
            AccountCreateDto createDto = AccountCreateDto.builder()
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .build();
            Account account = Account.builder()
                    .id(1L)
                    .accountNumber("1234567890123456")
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.ACTIVE)
                    .version(0)
                    .build();
            AccountViewDto accountViewDto = AccountViewDto.builder()
                    .accountNumber("1234567890123456")
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();

            when(accountHelper.generateAccountNumber()).thenReturn("1234567890123456");
            when(accountMapper.toEntity(createDto)).thenReturn(account);
            when(accountHelper.saveAccount(any(Account.class), anyString())).thenReturn(account);
            when(accountMapper.toViewDto(account)).thenReturn(accountViewDto);

            doNothing().when(balanceService).createBalanceForAccount(account.getId());
            AccountViewDto result = accountService.openAccount(createDto);

            assertEquals(accountViewDto, result);
            verify(accountHelper).generateAccountNumber();
            verify(accountMapper).toEntity(createDto);
            verify(accountHelper).saveAccount(any(Account.class), anyString());
            verify(balanceService).createBalanceForAccount(account.getId());
            verify(accountMapper).toViewDto(account);
        }
    }

    @Nested
    @DisplayName("Тесты для метода blockAccount")
    class BlockAccountTests {

        @Test
        @DisplayName("Успешная блокировка счета")
        void givenValidAccount_WhenBlockAccount_ThenReturnsBlockedAccountViewDto() {
            Long accountId = 1L;
            Account account = Account.builder()
                    .id(accountId)
                    .accountNumber("1234567890123456")
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();
            Account blockedAccount = Account.builder()
                    .id(accountId)
                    .accountNumber("1234567890123456")
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.BLOCKED)
                    .build();
            AccountViewDto accountViewDto = AccountViewDto.builder()
                    .accountNumber("1234567890123456")
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.BLOCKED)
                    .build();

            when(accountHelper.getAccountById(accountId)).thenReturn(account);
            doNothing().when(accountValidator).validateBlock(account);
            when(accountHelper.saveAccount(any(Account.class), anyString())).thenReturn(blockedAccount);
            when(accountMapper.toViewDto(blockedAccount)).thenReturn(accountViewDto);

            AccountViewDto result = accountService.blockAccount(accountId);

            assertEquals(accountViewDto, result);
            verify(accountHelper).getAccountById(accountId);
            verify(accountValidator).validateBlock(account);
            verify(accountHelper).saveAccount(any(Account.class), anyString());
            verify(accountMapper).toViewDto(blockedAccount);
        }

        @Test
        @DisplayName("Блокировка уже заблокированного счета")
        void givenAlreadyBlockedAccount_WhenBlockAccount_ThenThrowsConflictException() {
            Long accountId = 1L;
            Account account = Account.builder()
                    .id(accountId)
                    .accountNumber("1234567890123456")
                    .accountStatus(AccountStatus.BLOCKED)
                    .build();

            when(accountHelper.getAccountById(accountId)).thenReturn(account);
            doThrow(new AccountOperationConflictException("Account is already blocked"))
                    .when(accountValidator).validateBlock(account);

            assertThrows(AccountOperationConflictException.class, () -> accountService.blockAccount(accountId));
            verify(accountHelper).getAccountById(accountId);
            verify(accountValidator).validateBlock(account);
        }

        @Test
        @DisplayName("Блокировка закрытого счета")
        void givenClosedAccount_WhenBlockAccount_ThenThrowsClosedException() {
            Long accountId = 1L;
            Account account = Account.builder()
                    .id(accountId)
                    .accountNumber("1234567890123456")
                    .accountStatus(AccountStatus.CLOSED)
                    .build();

            when(accountHelper.getAccountById(accountId)).thenReturn(account);
            doThrow(new AccountAlreadyClosedException("Cannot modify closed account"))
                    .when(accountValidator).validateBlock(account);

            assertThrows(AccountAlreadyClosedException.class, () -> accountService.blockAccount(accountId));
            verify(accountHelper).getAccountById(accountId);
            verify(accountValidator).validateBlock(account);
        }
    }

    @Nested
    @DisplayName("Тесты для метода closeAccount")
    class CloseAccountTests {

        @Test
        @DisplayName("Успешное закрытие счета")
        void givenValidAccount_WhenCloseAccount_ThenReturnsClosedAccountViewDto() {
            Long accountId = 1L;
            Account account = Account.builder()
                    .id(accountId)
                    .accountNumber("1234567890123456")
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();
            Account closedAccount = Account.builder()
                    .id(accountId)
                    .accountNumber("1234567890123456")
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.CLOSED)
                    .closedAt(Instant.now())
                    .build();
            AccountViewDto accountViewDto = AccountViewDto.builder()
                    .accountNumber("1234567890123456")
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.CLOSED)
                    .closedAt(Instant.now())
                    .build();

            when(accountHelper.getAccountById(accountId)).thenReturn(account);
            doNothing().when(accountValidator).validateClose(account);
            when(accountHelper.saveAccount(any(Account.class), anyString())).thenReturn(closedAccount);
            when(accountMapper.toViewDto(closedAccount)).thenReturn(accountViewDto);

            AccountViewDto result = accountService.closeAccount(accountId);

            assertEquals(accountViewDto, result);
            verify(accountHelper).getAccountById(accountId);
            verify(accountValidator).validateClose(account);
            verify(accountHelper).saveAccount(any(Account.class), anyString());
            verify(accountMapper).toViewDto(closedAccount);
        }

        @Test
        @DisplayName("Закрытие счета с ненулевым балансом")
        void givenNonZeroBalanceAccount_WhenCloseAccount_ThenThrowsConflictException() {
            Long accountId = 1L;
            Account account = Account.builder()
                    .id(accountId)
                    .accountNumber("1234567890123456")
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();

            when(accountHelper.getAccountById(accountId)).thenReturn(account);
            doThrow(new AccountOperationConflictException("Cannot close account with non-zero balance"))
                    .when(accountValidator).validateClose(account);

            assertThrows(AccountOperationConflictException.class, () -> accountService.closeAccount(accountId));
            verify(accountHelper).getAccountById(accountId);
            verify(accountValidator).validateClose(account);
        }

        @Test
        @DisplayName("Закрытие уже закрытого счета")
        void givenAlreadyClosedAccount_WhenCloseAccount_ThenThrowsClosedException() {
            Long accountId = 1L;
            Account account = Account.builder()
                    .id(accountId)
                    .accountNumber("1234567890123456")
                    .accountStatus(AccountStatus.CLOSED)
                    .build();

            when(accountHelper.getAccountById(accountId)).thenReturn(account);
            doThrow(new AccountAlreadyClosedException("Cannot modify closed account"))
                    .when(accountValidator).validateClose(account);

            assertThrows(AccountAlreadyClosedException.class, () -> accountService.closeAccount(accountId));
            verify(accountHelper).getAccountById(accountId);
            verify(accountValidator).validateClose(account);
        }
    }

    @Nested
    @DisplayName("Тесты для метода unblockAccount")
    class UnblockAccountTests {

        @Test
        @DisplayName("Успешная разблокировка счета")
        void givenBlockedAccount_WhenUnblockAccount_ThenReturnsUnblockedAccountViewDto() {
            Long accountId = 1L;
            Account account = Account.builder()
                    .id(accountId)
                    .accountNumber("1234567890123456")
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.BLOCKED)
                    .build();
            Account unblockedAccount = Account.builder()
                    .id(accountId)
                    .accountNumber("1234567890123456")
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();
            AccountViewDto accountViewDto = AccountViewDto.builder()
                    .accountNumber("1234567890123456")
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();

            when(accountHelper.getAccountById(accountId)).thenReturn(account);
            doNothing().when(accountValidator).validateUnblock(account);
            when(accountHelper.saveAccount(any(Account.class), anyString())).thenReturn(unblockedAccount);
            when(accountMapper.toViewDto(unblockedAccount)).thenReturn(accountViewDto);

            AccountViewDto result = accountService.unblockAccount(accountId);

            assertEquals(accountViewDto, result);
            verify(accountHelper).getAccountById(accountId);
            verify(accountValidator).validateUnblock(account);
            verify(accountHelper).saveAccount(any(Account.class), anyString());
            verify(accountMapper).toViewDto(unblockedAccount);
        }

        @Test
        @DisplayName("Разблокировка незаблокированного счета")
        void givenNonBlockedAccount_WhenUnblockAccount_ThenThrowsConflictException() {
            Long accountId = 1L;
            Account account = Account.builder()
                    .id(accountId)
                    .accountNumber("1234567890123456")
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();

            when(accountHelper.getAccountById(accountId)).thenReturn(account);
            doThrow(new AccountOperationConflictException("Account is not blocked, cannot unblock"))
                    .when(accountValidator).validateUnblock(account);

            assertThrows(AccountOperationConflictException.class, () -> accountService.unblockAccount(accountId));
            verify(accountHelper).getAccountById(accountId);
            verify(accountValidator).validateUnblock(account);
        }

        @Test
        @DisplayName("Разблокировка закрытого счета")
        void givenClosedAccount_WhenUnblockAccount_ThenThrowsClosedException() {
            Long accountId = 1L;
            Account account = Account.builder()
                    .id(accountId)
                    .accountNumber("1234567890123456")
                    .accountStatus(AccountStatus.CLOSED)
                    .build();

            when(accountHelper.getAccountById(accountId)).thenReturn(account);
            doThrow(new AccountAlreadyClosedException("Cannot modify closed account"))
                    .when(accountValidator).validateUnblock(account);

            assertThrows(AccountAlreadyClosedException.class, () -> accountService.unblockAccount(accountId));
            verify(accountHelper).getAccountById(accountId);
            verify(accountValidator).validateUnblock(account);
        }
    }
}