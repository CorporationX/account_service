package faang.school.accountservice.service.account;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.AccountOperationConflictException;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для AccountHelper")
class AccountHelperTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountHelper accountHelper;

    @Nested
    @DisplayName("Тесты для метода getAccountById")
    class GetAccountByIdTests {

        @Test
        @DisplayName("Успешное получение счета по ID")
        void givenValidId_WhenGetAccountById_ThenReturnsAccount() {
            Long accountId = 1L;
            Account account = Account.builder()
                    .id(accountId)
                    .accountNumber("1234567890123456")
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.ACTIVE)
                    .balance(BigDecimal.ZERO)
                    .build();

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

            Account result = accountHelper.getAccountById(accountId);

            assertEquals(account, result);
            verify(accountRepository).findById(accountId);
        }

        @Test
        @DisplayName("Получение счета с несуществующим ID")
        void givenInvalidId_WhenGetAccountById_ThenThrowsNotFoundException() {
            Long accountId = 999L;
            when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

            AccountNotFoundException exception = assertThrows(AccountNotFoundException.class,
                    () -> accountHelper.getAccountById(accountId));
            assertEquals("Account not found with id: 999", exception.getMessage());
            verify(accountRepository).findById(accountId);
        }
    }

    @Nested
    @DisplayName("Тесты для метода generateAccountNumber")
    class GenerateAccountNumberTests {

        @Test
        @DisplayName("Успешная генерация уникального номера счета")
        void givenNoExistingNumber_WhenGenerateAccountNumber_ThenReturnsUniqueNumber() {
            when(accountRepository.existsByAccountNumber(any(String.class))).thenReturn(false);

            String result = accountHelper.generateAccountNumber();

            assertNotNull(result);
            assertTrue(result.length() >= 12 && result.length() <= 20);
            verify(accountRepository).existsByAccountNumber(any(String.class));
        }

        @Test
        @DisplayName("Генерация номера при существующем номере")
        void givenExistingNumber_WhenGenerateAccountNumber_ThenRetriesAndReturnsUniqueNumber() {
            AtomicInteger callCount = new AtomicInteger(0);
            when(accountRepository.existsByAccountNumber(any(String.class))).thenAnswer(invocation -> {
                int count = callCount.getAndIncrement();
                return count < 1;
            });

            String result = accountHelper.generateAccountNumber();

            assertNotNull(result);
            assertTrue(result.length() >= 12 && result.length() <= 20);
            verify(accountRepository, times(2)).existsByAccountNumber(any(String.class));
        }

        @Test
        @DisplayName("Превышение лимита попыток генерации номера")
        void givenAllNumbersExist_WhenGenerateAccountNumber_ThenThrowsIllegalStateException() {
            when(accountRepository.existsByAccountNumber(any(String.class))).thenReturn(true);

            IllegalStateException exception = assertThrows(IllegalStateException.class,
                    () -> accountHelper.generateAccountNumber());
            assertEquals("Не удалось сгенерировать уникальный номер счёта", exception.getMessage());
            verify(accountRepository, times(11)).existsByAccountNumber(any(String.class));
        }
    }

    @Nested
    @DisplayName("Тесты для метода saveAccount")
    class SaveAccountTests {

        @Test
        @DisplayName("Успешное сохранение счета")
        void givenValidAccount_WhenSaveAccount_ThenReturnsSavedAccount() {
            Account account = Account.builder()
                    .id(1L)
                    .accountNumber("1234567890123456")
                    .ownerType(OwnerType.USER)
                    .ownerId(1L)
                    .accountType(AccountType.PERSONAL_SETTLEMENT)
                    .currency(Currency.USD)
                    .accountStatus(AccountStatus.ACTIVE)
                    .balance(BigDecimal.ZERO)
                    .build();

            when(accountRepository.save(account)).thenReturn(account);

            Account result = accountHelper.saveAccount(account);

            assertEquals(account, result);
            verify(accountRepository).save(account);
        }

        @Test
        @DisplayName("Сохранение счета с конфликтом оптимистической блокировки")
        void givenAccountWithConflict_WhenSaveAccount_ThenThrowsConflictException() {
            Account account = Account.builder()
                    .id(1L)
                    .accountNumber("1234567890123456")
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();

            when(accountRepository.save(account))
                    .thenThrow(new ObjectOptimisticLockingFailureException("Optimistic locking failure", null));

            AccountOperationConflictException exception = assertThrows(AccountOperationConflictException.class,
                    () -> accountHelper.saveAccount(account));
            verify(accountRepository).save(account);
        }
    }
}