package faang.school.accountservice.validation;

import faang.school.accountservice.exception.InsufficientFundsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class Balance2ValidatorTest {

    @InjectMocks
    private BalanceValidator2 balanceValidator2;

    @Test
    @DisplayName("Когда баланс достаточный, исключения не должно быть")
    public void givenSufficientBalance_whenValidateActualBalanceSufficiency_thenNoException() {
        BigDecimal balance = new BigDecimal("1000.00");
        BigDecimal amount = new BigDecimal("500.00");

        assertDoesNotThrow(() -> balanceValidator2.validateActualBalanceSufficiency(balance, amount));
    }

    @Test
    @DisplayName("Когда баланс недостаточный, должно выброситься исключение")
    public void givenInsufficientBalance_whenValidateActualBalanceSufficiency_thenThrowException() {
        BigDecimal balance = new BigDecimal("300.00");
        BigDecimal amount = new BigDecimal("500.00");

        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class,
                () -> balanceValidator2.validateActualBalanceSufficiency(balance, amount));
        assertTrue(exception.getMessage().contains(String.format("Insufficient account balance. Available: %s, Required: %s",
                balance, amount)));
    }

    @Test
    @DisplayName("Когда баланс равен сумме, не должно быть исключения")
    public void givenEqualBalanceAndAmount_whenValidateActualBalanceSufficiency_thenNoException() {
        BigDecimal balance = new BigDecimal("500.00");
        BigDecimal amount = new BigDecimal("500.00");

        assertDoesNotThrow(() -> balanceValidator2.validateActualBalanceSufficiency(balance, amount));
    }

    @Test
    @DisplayName("Когда авторизационный баланс достаточный, не должно быть исключения")
    public void givenSufficientAuthBalance_whenValidateAuthorizedBalanceSufficiency_thenNoException() {// given
        BigDecimal authBalance = new BigDecimal("800.00");
        BigDecimal amount = new BigDecimal("400.00");

        assertDoesNotThrow(() -> balanceValidator2.validateAuthorizedBalanceSufficiency(authBalance, amount));
    }

    @Test
    @DisplayName("Когда авторизационный баланс недостаточный, должно выброситься исключение")
    public void givenInsufficientAuthBalance_whenValidateAuthorizedBalanceSufficiency_thenThrowException() {
        BigDecimal authBalance = new BigDecimal("200.00");
        BigDecimal amount = new BigDecimal("500.00");

        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class,
                () -> balanceValidator2.validateAuthorizedBalanceSufficiency(authBalance, amount));
        assertTrue(exception.getMessage().contains(String.format("Insufficient reserved funds. Available: %s, Required: %s",
                authBalance, amount)));
    }
}
