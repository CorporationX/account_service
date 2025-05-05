package faang.school.accountservice.validation;

import faang.school.accountservice.dto.Currency;
import faang.school.accountservice.exception.BalanceValidationException;
import faang.school.accountservice.model.AccountOperation;
import faang.school.accountservice.model.Balance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class BalanceValidatorTest {

    private BalanceValidator balanceValidator;
    private Balance senderBalance;
    private Balance recipientBalance;
    private AccountOperation operation;
    private BigDecimal amount;
    private UUID accountId1;
    private UUID accountId2;

    @BeforeEach
    void setUp() {
        balanceValidator = new BalanceValidator();

        accountId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        accountId2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");

        senderBalance = new Balance();
        senderBalance.setAccountId(accountId1);
        senderBalance.setCurrency(Currency.USD);
        senderBalance.setClearBalance(new BigDecimal("1000"));
        senderBalance.setAuthBalance(new BigDecimal("500"));

        recipientBalance = new Balance();
        recipientBalance.setAccountId(accountId2);
        recipientBalance.setCurrency(Currency.USD);
        recipientBalance.setClearBalance(new BigDecimal("2000"));
        recipientBalance.setAuthBalance(new BigDecimal("1000"));

        operation = new AccountOperation();
        operation.setCurrency(Currency.USD);
        operation.setAmount(new BigDecimal("500"));
        operation.setSenderAccountId(accountId1);

        amount = new BigDecimal("400");
    }

    @Nested
    class authAuthorizationTest {

        @Test
        void givenValidData_whenAuthValidation_thenSuccess() {
            assertDoesNotThrow(() -> balanceValidator.authValidation(senderBalance, operation));
        }

        @Test
        void givenCurrencyMismatch_whenAuthValidation_thenThrowsException() {
            operation.setCurrency(Currency.EUR);

            BalanceValidationException exception = assertThrows(BalanceValidationException.class,
                    () -> balanceValidator.authValidation(senderBalance, operation));
            assertEquals("Account currency USD doesn't match operation currency EUR", exception.getMessage());
        }

        @Test
        void givenInsufficientFunds_whenAuthValidation_ThrowsException() {
            senderBalance.setClearBalance(new BigDecimal("400"));

            BalanceValidationException exception = assertThrows(BalanceValidationException.class,
                    () -> balanceValidator.authValidation(senderBalance, operation));
            assertEquals("Insufficient funds in account " + accountId1 + ". Available: 400, Required: 500",
                    exception.getMessage());
        }
    }

    @Nested
    class ClearValidationTest {

        @Test
        void givenValidData_whenClearValidation_thenSuccess() {
            assertDoesNotThrow(() -> balanceValidator.clearValidation(senderBalance, recipientBalance, amount));
        }

        @Test
        void givenCurrencyMismatch_whenClearValidation_thenThrowsException() {
            recipientBalance.setCurrency(Currency.EUR);

            BalanceValidationException exception = assertThrows(BalanceValidationException.class,
                    () -> balanceValidator.clearValidation(senderBalance, recipientBalance, amount));
            assertEquals("Currency mismatch between accounts. Sender: USD, Recipient: EUR", exception.getMessage());
        }

        @Test
        void givenInsufficientFunds_whenClearValidation_thenThrowsException() {
            senderBalance.setAuthBalance(new BigDecimal("300"));

            BalanceValidationException exception = assertThrows(BalanceValidationException.class,
                    () -> balanceValidator.clearValidation(senderBalance, recipientBalance, amount));
            assertEquals("Insufficient reserved funds in account " + accountId1 + " for clearing. Available: 300, Required: 400",
                    exception.getMessage());
        }
    }

    @Nested
    class CancelValidationTest {

        @Test
        void givenValidData_whenCancelValidation_thenSuccess() {
            assertDoesNotThrow(() -> balanceValidator.cancelValidation(senderBalance, amount));
        }

        @Test
        void givenInsufficientFunds_whenCancelValidation_thenThrowsException() {
            senderBalance.setAuthBalance(new BigDecimal("300"));

            BalanceValidationException exception = assertThrows(BalanceValidationException.class,
                    () -> balanceValidator.cancelValidation(senderBalance, amount));
            assertEquals("Insufficient reserved funds in account " + accountId1 + " for cancellation. Available: 300, Required: 400",
                    exception.getMessage());
        }
    }
}
