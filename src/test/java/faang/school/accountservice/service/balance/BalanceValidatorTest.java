package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.exception.ValidationException;
import faang.school.accountservice.entity.AuthPayment;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.non_retryable.NotEnoughFundsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static faang.school.accountservice.entity.AuthPaymentStatus.ACTIVE;
import static faang.school.accountservice.entity.AuthPaymentStatus.CLOSED;
import static faang.school.accountservice.util.AuthPaymentFabrics.buildAuthPayment;
import static faang.school.accountservice.util.BalanceFabrics.buildBalance;
import static faang.school.accountservice.util.ManyFabrics.buildMoney;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class BalanceValidatorTest {

    private static final UUID BALANCE_ID = UUID.randomUUID();
    private static final UUID AUTH_PAYMENT_ID = UUID.randomUUID();

    private final BalanceValidator balanceValidator = new BalanceValidator();

    @Test
    @DisplayName("Check free amount failed and throw exception")
    void testCheckFreeAmountThrowException() {
        double currentBalance = 4.0;
        double authBalance = 2.0;
        double moneyAmount = 4.0;
        Balance balance = buildBalance(BALANCE_ID, currentBalance, authBalance);
        Money money = buildMoney(moneyAmount);

        assertThatThrownBy(() -> balanceValidator.checkFreeAmount(balance, money))
                .isInstanceOf(NotEnoughFundsException.class)
                .hasMessageContaining("Not enough funds to authorize the amount: %s", money.amount());
    }

    @Test
    @DisplayName("Check free amount successful")
    void testCheckFreeAmountThrowSuccessful() {
        double currentBalance = 3.0;
        double authBalance = 1.0;
        double moneyAmount = 2.0;
        Balance balance = buildBalance(BALANCE_ID, currentBalance, authBalance);
        Money money = buildMoney(moneyAmount);

        balanceValidator.checkFreeAmount(balance, money);
    }

    @Test
    @DisplayName("Check auth payment status failed and throw exception")
    void testCheckAuthPaymentThrowStatusException() {
        double paymentAmount = 1.0;
        double moneyAmount = 2.0;
        AuthPayment authPayment = buildAuthPayment(AUTH_PAYMENT_ID, paymentAmount, CLOSED);
        Money money = buildMoney(moneyAmount);

        assertThatThrownBy(() -> balanceValidator.checkAuthPaymentForAccept(money, authPayment))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("AuthPayment with id : %s will not accepted, current status: %s",
                        authPayment.getId(), authPayment.getStatus());
    }

    @Test
    @DisplayName("Check auth payment amount failed and throw exception")
    void testCheckAuthPaymentThrowAmountException() {
        double paymentAmount = 1.0;
        double moneyAmount = 2.0;
        AuthPayment authPayment = buildAuthPayment(AUTH_PAYMENT_ID, paymentAmount, ACTIVE);
        Money money = buildMoney(moneyAmount);

        assertThatThrownBy(() -> balanceValidator.checkAuthPaymentForAccept(money, authPayment))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Money has amount more than limit: %s of payment with id: %s",
                        authPayment.getAmount(), authPayment.getId());
    }

    @Test
    @DisplayName("Check auth payment successful")
    void testCheckAuthPaymentSuccessful() {
        double paymentAmount = 1.0;
        double moneyAmount = 1.0;
        AuthPayment authPayment = buildAuthPayment(AUTH_PAYMENT_ID, paymentAmount, ACTIVE);
        Money money = buildMoney(moneyAmount);

        balanceValidator.checkAuthPaymentForAccept(money, authPayment);

        assertDoesNotThrow(() -> balanceValidator.checkAuthPaymentForAccept(money, authPayment));
    }

    @Test
    @DisplayName("Check auth payment for reject failed and throw exception")
    void testCheckAuthPaymentForRejectThrowStatusException() {
        AuthPayment payment = buildAuthPayment(CLOSED);

        assertThatThrownBy(() -> balanceValidator.checkAuthPaymentForReject(payment))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("AuthPayment with id: %s will not rejected, current status: %s",
                        payment.getId(), payment.getStatus());
    }
}
