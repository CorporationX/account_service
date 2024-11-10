package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.entity.auth.payment.AuthPayment;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static faang.school.accountservice.enums.auth.payment.AuthPaymentStatus.ACTIVE;
import static faang.school.accountservice.enums.auth.payment.AuthPaymentStatus.CLOSED;
import static faang.school.accountservice.util.fabrics.AuthPaymentFabric.buildAuthPayment;
import static faang.school.accountservice.util.fabrics.BalanceFabric.buildBalance;
import static faang.school.accountservice.util.fabrics.MoneyFabric.buildMoney;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class BalanceValidatorTest {
    private static final UUID OPERATION_ID = UUID.randomUUID();
    private static final UUID BALANCE_ID = UUID.randomUUID();
    private static final UUID AUTH_PAYMENT_ID = UUID.randomUUID();

    private final BalanceValidator balanceValidator = new BalanceValidator();

    @Test
    void testCheckFreeAmount_exception() {
        double currentBalance = 1.0;
        double authBalance = 1.0;
        double moneyAmount = 2.0;
        Balance sourceBalance = buildBalance(BALANCE_ID, currentBalance, authBalance);
        Money money = buildMoney(moneyAmount);

        assertThatThrownBy(() -> balanceValidator.checkFreeAmount(OPERATION_ID, sourceBalance, money))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Not enough funds, amount: %s, operation id: %s, balance id: %s",
                        money.amount(), OPERATION_ID, BALANCE_ID);
    }

    @Test
    void testCheckFreeAmount_successful() {
        double currentBalance = 3.0;
        double authBalance = 1.0;
        double moneyAmount = 2.0;
        Balance balance = buildBalance(BALANCE_ID, authBalance, currentBalance);
        Money money = buildMoney(moneyAmount);

        assertDoesNotThrow(() -> balanceValidator.checkFreeAmount(OPERATION_ID, balance, money));
    }

    @Test
    void testCheckAuthPaymentForAccept_exception() {
        double paymentAmount = 1.0;
        AuthPayment authPayment = buildAuthPayment(AUTH_PAYMENT_ID, paymentAmount, CLOSED);

        assertThatThrownBy(() -> balanceValidator.checkAuthPaymentForAccept(authPayment))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("AuthPayment with id: %s will not accepted, current status: %s",
                        authPayment.getId(), authPayment.getStatus());
    }

    @Test
    void testCheckAuthPaymentForAccept_successful() {
        double paymentAmount = 1.0;
        AuthPayment authPayment = buildAuthPayment(AUTH_PAYMENT_ID, paymentAmount, ACTIVE);

        assertDoesNotThrow(() -> balanceValidator.checkAuthPaymentForAccept(authPayment));
    }

    @Test
    void testCheckAuthPaymentForReject_exception() {
        AuthPayment payment = buildAuthPayment(CLOSED);

        assertThatThrownBy(() -> balanceValidator.checkAuthPaymentForReject(payment))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("AuthPayment with id: %s will not rejected, current status: %s",
                        payment.getId(), payment.getStatus());
    }

    @Test
    void testCheckAuthPaymentForReject_successful() {
        AuthPayment payment = buildAuthPayment(ACTIVE);

        assertDoesNotThrow(() -> balanceValidator.checkAuthPaymentForReject(payment));
    }
}
