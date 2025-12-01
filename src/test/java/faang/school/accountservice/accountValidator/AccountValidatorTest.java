package faang.school.accountservice.accountValidator;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.InvalidAccountOperationException;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.validator.AccountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountValidatorTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountValidator accountValidator;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .id(1L)
                .accountNumber("12345678901234567890")
                .ownerId(1L)
                .ownerType(OwnerType.USER)
                .accountType(AccountType.PERSONAL_CHECKING)
                .currency(Currency.USD)
                .status(AccountStatus.ACTIVE)
                .balance( new Balance())
                .version(0L)
                .build();
    }

    @Test
    @DisplayName("Валидация открытия счета - успех")
    void testValidateAccountOpening_Success() {
        // Given
        when(accountRepository.countActiveAccountsByOwner(anyLong(), any(OwnerType.class)))
                .thenReturn(5L);
        when(accountRepository.existsByOwnerIdAndOwnerTypeAndCurrencyAndStatus(
                anyLong(), any(OwnerType.class), any(Currency.class), any(AccountStatus.class)))
                .thenReturn(false);

        // When & Then - не должно быть исключений
        accountValidator.validateAccountOpening(1L, OwnerType.USER, Currency.USD);
    }

    @Test
    @DisplayName("Валидация открытия счета - превышен лимит")
    void testValidateAccountOpening_ExceedsLimit() {
        // Given
        when(accountRepository.countActiveAccountsByOwner(anyLong(), any(OwnerType.class)))
                .thenReturn(10L);

        // When & Then
        assertThatThrownBy(() -> accountValidator.validateAccountOpening(
                1L, OwnerType.USER, Currency.USD))
                .isInstanceOf(InvalidAccountOperationException.class)
                .hasMessageContaining("maximum limit");
    }

    @Test
    @DisplayName("Валидация блокировки счета - успех")
    void testValidateBlockAccount_Success() {
        // When & Then
        accountValidator.validateBlockAccount(testAccount);
    }

    @Test
    @DisplayName("Валидация блокировки закрытого счета - ошибка")
    void testValidateBlockAccount_ClosedAccount() {
        // Given
        testAccount.setStatus(AccountStatus.CLOSED);

        // When & Then
        assertThatThrownBy(() -> accountValidator.validateBlockAccount(testAccount))
                .isInstanceOf(InvalidAccountOperationException.class)
                .hasMessageContaining("Cannot block a closed account");
    }

    @Test
    @DisplayName("Валидация блокировки уже заблокированного счета - ошибка")
    void testValidateBlockAccount_AlreadyBlocked() {
        // Given
        testAccount.setStatus(AccountStatus.BLOCKED);

        // When & Then
        assertThatThrownBy(() -> accountValidator.validateBlockAccount(testAccount))
                .isInstanceOf(InvalidAccountOperationException.class)
                .hasMessageContaining("already blocked");
    }

    @Test
    @DisplayName("Валидация заморозки счета - успех")
    void testValidateFreezeAccount_Success() {
        // When & Then
        accountValidator.validateFreezeAccount(testAccount);
    }

    @Test
    @DisplayName("Валидация разморозки счета - успех")
    void testValidateUnfreezeAccount_Success() {
        // Given
        testAccount.setStatus(AccountStatus.FROZEN);

        // When & Then
        accountValidator.validateUnfreezeAccount(testAccount);
    }

    @Test
    @DisplayName("Валидация разморозки незамороженного счета - ошибка")
    void testValidateUnfreezeAccount_NotFrozen() {
        // When & Then
        assertThatThrownBy(() -> accountValidator.validateUnfreezeAccount(testAccount))
                .isInstanceOf(InvalidAccountOperationException.class)
                .hasMessageContaining("not frozen");
    }




    @Test
    @DisplayName("Валидация номера счета - успех")
    void testValidateAccountNumber_Success() {
        // When & Then
        accountValidator.validateAccountNumber("12345678901234567890");
        accountValidator.validateAccountNumber("123456789012"); // минимум 12 цифр
    }

    @Test
    @DisplayName("Валидация номера счета - слишком короткий")
    void testValidateAccountNumber_TooShort() {
        // When & Then
        assertThatThrownBy(() -> accountValidator.validateAccountNumber("12345678901"))
                .isInstanceOf(InvalidAccountOperationException.class)
                .hasMessageContaining("12 to 20 digits");
    }

    @Test
    @DisplayName("Валидация номера счета - слишком длинный")
    void testValidateAccountNumber_TooLong() {
        // When & Then
        assertThatThrownBy(() -> accountValidator.validateAccountNumber("123456789012345678901"))
                .isInstanceOf(InvalidAccountOperationException.class)
                .hasMessageContaining("12 to 20 digits");
    }

    @Test
    @DisplayName("Валидация номера счета - содержит буквы")
    void testValidateAccountNumber_ContainsLetters() {
        // When & Then
        assertThatThrownBy(() -> accountValidator.validateAccountNumber("1234567890ABCDEF1234"))
                .isInstanceOf(InvalidAccountOperationException.class)
                .hasMessageContaining("digits only");
    }
}