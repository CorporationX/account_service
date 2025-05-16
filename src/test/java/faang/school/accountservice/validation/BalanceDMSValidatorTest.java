package faang.school.accountservice.validation;

import faang.school.accountservice.dto.Currency;
import faang.school.accountservice.exception.BalanceValidationException;
import faang.school.accountservice.model.AccountOperation;
import faang.school.accountservice.model.BalanceDMS;
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
public class BalanceDMSValidatorTest {

    private BalanceDMSValidator balanceDMSValidator;
    private BalanceDMS senderBalanceDMS;
    private BalanceDMS recipientBalanceDMS;
    private AccountOperation operation;
    private BigDecimal amount;
    private UUID accountId1;
    private UUID accountId2;

    @BeforeEach
    void setUp() {
        balanceDMSValidator = new BalanceDMSValidator();

        accountId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        accountId2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");

        senderBalanceDMS = new BalanceDMS();
        senderBalanceDMS.setAccountId(accountId1);
        senderBalanceDMS.setCurrency(Currency.USD);
        senderBalanceDMS.setClearBalance(new BigDecimal("1000"));
        senderBalanceDMS.setAuthBalance(new BigDecimal("500"));

        recipientBalanceDMS = new BalanceDMS();
        recipientBalanceDMS.setAccountId(accountId2);
        recipientBalanceDMS.setCurrency(Currency.USD);
        recipientBalanceDMS.setClearBalance(new BigDecimal("2000"));
        recipientBalanceDMS.setAuthBalance(new BigDecimal("1000"));

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
            assertDoesNotThrow(() -> balanceDMSValidator.authValidation(senderBalanceDMS, operation));
        }

        @Test
        void givenCurrencyMismatch_whenAuthValidation_thenThrowsException() {
            operation.setCurrency(Currency.EUR);

            BalanceValidationException exception = assertThrows(BalanceValidationException.class,
                    () -> balanceDMSValidator.authValidation(senderBalanceDMS, operation));
            assertEquals("Account currency USD doesn't match operation currency EUR", exception.getMessage());
        }

        @Test
        void givenInsufficientFunds_whenAuthValidation_ThrowsException() {
            senderBalanceDMS.setClearBalance(new BigDecimal("400"));

            BalanceValidationException exception = assertThrows(BalanceValidationException.class,
                    () -> balanceDMSValidator.authValidation(senderBalanceDMS, operation));
            assertEquals("Insufficient funds in account " + accountId1 + ". Available: 400, Required: 500",
                    exception.getMessage());
        }
    }

    @Nested
    class ClearValidationTest {

        @Test
        void givenValidData_whenClearValidation_thenSuccess() {
            assertDoesNotThrow(() -> balanceDMSValidator.clearValidation(senderBalanceDMS, recipientBalanceDMS, amount));
        }

        @Test
        void givenCurrencyMismatch_whenClearValidation_thenThrowsException() {
            recipientBalanceDMS.setCurrency(Currency.EUR);

            BalanceValidationException exception = assertThrows(BalanceValidationException.class,
                    () -> balanceDMSValidator.clearValidation(senderBalanceDMS, recipientBalanceDMS, amount));
            assertEquals("Currency mismatch between accounts. Sender: USD, Recipient: EUR", exception.getMessage());
        }

        @Test
        void givenInsufficientFunds_whenClearValidation_thenThrowsException() {
            senderBalanceDMS.setAuthBalance(new BigDecimal("300"));

            BalanceValidationException exception = assertThrows(BalanceValidationException.class,
                    () -> balanceDMSValidator.clearValidation(senderBalanceDMS, recipientBalanceDMS, amount));
            assertEquals("Insufficient reserved funds in account " + accountId1 + " for clearing. Available: 300, Required: 400",
                    exception.getMessage());
        }
    }

    @Nested
    class CancelValidationTest {

        @Test
        void givenValidData_whenCancelValidation_thenSuccess() {
            assertDoesNotThrow(() -> balanceDMSValidator.cancelValidation(senderBalanceDMS, amount));
        }

        @Test
        void givenInsufficientFunds_whenCancelValidation_thenThrowsException() {
            senderBalanceDMS.setAuthBalance(new BigDecimal("300"));

            BalanceValidationException exception = assertThrows(BalanceValidationException.class,
                    () -> balanceDMSValidator.cancelValidation(senderBalanceDMS, amount));
            assertEquals("Insufficient reserved funds in account " + accountId1 + " for cancellation. Available: 300, Required: 400",
                    exception.getMessage());
        }
    }
}
