package faang.school.accountservice.service.balance;

import com.github.f4b6a3.uuid.UuidCreator;
import faang.school.accountservice.dto.Money;
import faang.school.accountservice.exception.ValidationException;
import faang.school.accountservice.model.balance.AuthPayment;
import faang.school.accountservice.model.balance.Balance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static faang.school.accountservice.model.balance.AuthPaymentStatus.ACTIVE;
import static faang.school.accountservice.model.balance.AuthPaymentStatus.CLOSED;
import static faang.school.accountservice.util.fabrics.AuthPaymentFabric.buildAuthPayment;
import static faang.school.accountservice.util.fabrics.BalanceFabric.buildBalance;
import static faang.school.accountservice.util.fabrics.MoneyFabric.buildMoney;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BalanceValidatorTest {
    private static final UUID BALANCE_ID = UuidCreator.getTimeBased();
    private static final UUID AUTH_PAYMENT_ID = UuidCreator.getTimeBased();

    private final BalanceValidator balanceValidator = new BalanceValidator();

    @Test
    @DisplayName("Check free amount failed and throw exception")
    void testCheckFreeAmountThrowException() {
        double currentBalance = 2.0;
        double authBalance = 1.0;
        double moneyAmount = 2.0;
        Balance balance = buildBalance(BALANCE_ID, currentBalance, authBalance);
        Money money = buildMoney(moneyAmount);

        assertThatThrownBy(() -> balanceValidator.checkFreeAmount(balance, money))
                .isInstanceOf(ValidationException.class)
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
                .hasMessageContaining("AuthPayment with id: %s will not accepted, current status: %s",
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