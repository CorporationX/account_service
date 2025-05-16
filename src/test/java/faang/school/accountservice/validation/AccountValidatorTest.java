package faang.school.accountservice.validation;

import faang.school.accountservice.dto.balance.BalanceViewDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.AccountOperationConflictException;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.account.AccountHelper;
import faang.school.accountservice.service.balance.BalanceService2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для AccountValidator")
class AccountValidatorTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountHelper accountHelper;

    @Mock
    private BalanceService2 balanceService2;

    @InjectMocks
    private AccountValidator accountValidator;

    @Nested
    @DisplayName("Тесты для метода validateStatus")
    class ValidateStatusTests {

        @Test
        @DisplayName("Валидация изменения на новый статус")
        void givenActiveAccountAndNewStatus_WhenValidateStatus_ThenPasses() {
            Account account = Account.builder()
                    .id(1L)
                    .accountNumber("1234567890123456")
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();

            assertDoesNotThrow(() -> accountValidator.validateStatus(account, AccountStatus.BLOCKED));
        }

        @Test
        @DisplayName("Валидация изменения на тот же статус")
        void givenActiveAccountAndSameStatus_WhenValidateStatus_ThenThrowsConflictException() {
            Account account = Account.builder()
                    .id(1L)
                    .accountNumber("1234567890123456")
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();

            AccountOperationConflictException exception = assertThrows(AccountOperationConflictException.class,
                    () -> accountValidator.validateStatus(account, AccountStatus.ACTIVE));
            assertEquals("Account is already in the requested state", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Тесты для метода validateBlock")
    class ValidateBlockTests {

        @Test
        @DisplayName("Успешная валидация для блокировки активного счета")
        void givenActiveAccount_WhenValidateBlock_ThenPasses() {
            Account account = Account.builder()
                    .id(1L)
                    .accountNumber("1234567890123456")
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();

            assertDoesNotThrow(() -> accountValidator.validateBlock(account));
        }

        @Test
        @DisplayName("Блокировка уже заблокированного счета")
        void givenBlockedAccount_WhenValidateBlock_ThenThrowsConflictException() {
            Account account = Account.builder()
                    .id(1L)
                    .accountNumber("1234567890123456")
                    .accountStatus(AccountStatus.BLOCKED)
                    .build();

            AccountOperationConflictException exception = assertThrows(AccountOperationConflictException.class,
                    () -> accountValidator.validateBlock(account));
            assertEquals("Account is already in the requested state", exception.getMessage());
        }

        @Test
        @DisplayName("Блокировка закрытого счета")
        void givenClosedAccount_WhenValidateBlock_ThenThrowsClosedException() {
            Account account = Account.builder()
                    .id(1L)
                    .accountNumber("1234567890123456")
                    .accountStatus(AccountStatus.CLOSED)
                    .build();

            AccountOperationConflictException exception = assertThrows(AccountOperationConflictException.class,
                    () -> accountValidator.validateBlock(account));
            assertEquals("Account is already in the requested state", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Тесты для метода validateUnblock")
    class ValidateUnblockTests {

        @Test
        @DisplayName("Успешная валидация для разблокировки заблокированного счета")
        void givenBlockedAccount_WhenValidateUnblock_ThenPasses() {
            Account account = Account.builder()
                    .id(1L)
                    .accountNumber("1234567890123456")
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.BLOCKED)
                    .build();

            assertDoesNotThrow(() -> accountValidator.validateUnblock(account));
        }

        @Test
        @DisplayName("Разблокировка незаблокированного счета")
        void givenNonBlockedAccount_WhenValidateUnblock_ThenThrowsConflictException() {
            Account account = Account.builder()
                    .id(1L)
                    .accountNumber("1234567890123456")
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();

            AccountOperationConflictException exception = assertThrows(AccountOperationConflictException.class,
                    () -> accountValidator.validateUnblock(account));
            assertEquals("Account is not blocked, cannot unblock", exception.getMessage());
        }

        @Test
        @DisplayName("Разблокировка закрытого счета")
        void givenClosedAccount_WhenValidateUnblock_ThenThrowsClosedException() {
            Account account = Account.builder()
                    .id(1L)
                    .accountNumber("1234567890123456")
                    .accountStatus(AccountStatus.CLOSED)
                    .build();

            AccountOperationConflictException exception = assertThrows(AccountOperationConflictException.class,
                    () -> accountValidator.validateUnblock(account));
            assertEquals("Account is already in the requested state", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Тесты для метода validateClose")
    class ValidateCloseTests {

        @Test
        @DisplayName("Успешная валидация для закрытия счета с нулевым балансом")
        void givenZeroBalanceAccount_WhenValidateClose_ThenPasses() {
            BalanceViewDto balanceViewDto = new BalanceViewDto();
            balanceViewDto.setActualBalance(BigDecimal.ZERO);
            Account account = Account.builder()
                    .id(1L)
                    .accountNumber("1234567890123456")
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();
            when(balanceService2.getBalance(account.getId())).thenReturn(balanceViewDto);

            assertDoesNotThrow(() -> accountValidator.validateClose(account));
        }

        @Test
        @DisplayName("Закрытие счета с ненулевым балансом")
        void givenNonZeroBalanceAccount_WhenValidateClose_ThenThrowsConflictException() {
            BalanceViewDto balanceViewDto = new BalanceViewDto();
            balanceViewDto.setActualBalance(BigDecimal.TEN);
            Account account = Account.builder()
                    .id(1L)
                    .accountNumber("1234567890123456")
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();
            when(balanceService2.getBalance(account.getId())).thenReturn(balanceViewDto);

            AccountOperationConflictException exception = assertThrows(AccountOperationConflictException.class,
                    () -> accountValidator.validateClose(account));
            assertEquals("Cannot close account with non-zero balance", exception.getMessage());
        }

        @Test
        @DisplayName("Закрытие уже закрытого счета")
        void givenClosedAccount_WhenValidateClose_ThenThrowsClosedException() {
            Account account = Account.builder()
                    .id(1L)
                    .accountNumber("1234567890123456")
                    .accountStatus(AccountStatus.CLOSED)
                    .build();

            AccountOperationConflictException exception = assertThrows(AccountOperationConflictException.class,
                    () -> accountValidator.validateClose(account));
            assertEquals("Account is already in the requested state", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Тесты для метода validateNotClosed")
    class ValidateNotClosedTests {

        @Test
        @DisplayName("Валидация незакрытого счета")
        void givenNonClosedAccount_WhenValidateNotClosed_ThenPasses() {
            Account account = Account.builder()
                    .id(1L)
                    .accountNumber("1234567890123456")
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();

            assertDoesNotThrow(() -> accountValidator.validateNotClosed(account));
        }

        @Test
        @DisplayName("Валидация закрытого счета")
        void givenClosedAccount_WhenValidateNotClosed_ThenThrowsClosedException() {
            Account account = Account.builder()
                    .id(1L)
                    .accountNumber("1234567890123456")
                    .accountStatus(AccountStatus.CLOSED)
                    .build();

            AccountOperationConflictException exception = assertThrows(AccountOperationConflictException.class,
                    () -> accountValidator.validateNotClosed(account));
            assertEquals("Account is already in the requested state", exception.getMessage());
        }
    }
}