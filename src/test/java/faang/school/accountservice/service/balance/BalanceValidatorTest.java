package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.dto.balance.response.CheckingAccountBalance;
import faang.school.accountservice.dto.listener.pending.OperationMessage;
import faang.school.accountservice.exception.ValidationException;
import faang.school.accountservice.entity.auth.payment.AuthPayment;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.publisher.pending.PendingOperationStatusPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static faang.school.accountservice.enums.auth.payment.AuthPaymentStatus.ACTIVE;
import static faang.school.accountservice.enums.auth.payment.AuthPaymentStatus.CLOSED;
import static faang.school.accountservice.enums.pending.AccountBalanceStatus.INSUFFICIENT_FUNDS;
import static faang.school.accountservice.util.fabrics.AuthPaymentFabric.buildAuthPayment;
import static faang.school.accountservice.util.fabrics.BalanceFabric.buildBalance;
import static faang.school.accountservice.util.fabrics.MoneyFabric.buildMoney;
import static faang.school.accountservice.util.fabrics.OperationMessageFabric.buildOperationMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BalanceValidatorTest {
    private static final UUID OPERATION_ID = UUID.randomUUID();
    private static final UUID ACCOUNT_ID = UUID.randomUUID();
    private static final UUID BALANCE_ID = UUID.randomUUID();
    private static final UUID AUTH_PAYMENT_ID = UUID.randomUUID();

    @Mock
    private PendingOperationStatusPublisher pendingOperationStatusPublisher;

    @InjectMocks
    private BalanceValidator balanceValidator;

    @Test
    @DisplayName("Check free amount failed and throw exception")
    void testCheckFreeAmountThrowException() {
        double currentBalance = 2.0;
        double authBalance = 1.0;
        double moneyAmount = 2.0;
        OperationMessage operation = buildOperationMessage(OPERATION_ID, ACCOUNT_ID);
        Balance balance = buildBalance(BALANCE_ID, currentBalance, authBalance);
        Money money = buildMoney(moneyAmount);

        assertThatThrownBy(() -> balanceValidator.checkFreeAmount(operation, balance, money))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Not enough funds, amount: %s, operation id: %s, balance id: %s",
                        money.amount(), operation.getOperationId(), balance.getId());

        ArgumentCaptor<CheckingAccountBalance> checkingAccountBalanceCaptor =
                ArgumentCaptor.forClass(CheckingAccountBalance.class);

        verify(pendingOperationStatusPublisher).publish(checkingAccountBalanceCaptor.capture());

        CheckingAccountBalance checkingAccountBalance = checkingAccountBalanceCaptor.getValue();
        assertThat(checkingAccountBalance.getOperationId()).isEqualTo(OPERATION_ID);
        assertThat(checkingAccountBalance.getSourceAccountId()).isEqualTo(ACCOUNT_ID);
        assertThat(checkingAccountBalance.getStatus()).isEqualTo(INSUFFICIENT_FUNDS);
    }

    @Test
    @DisplayName("Check free amount successful")
    void testCheckFreeAmountThrowSuccessful() {
        double currentBalance = 3.0;
        double authBalance = 1.0;
        double moneyAmount = 2.0;
        OperationMessage operation = buildOperationMessage(OPERATION_ID, ACCOUNT_ID);
        Balance balance = buildBalance(BALANCE_ID, currentBalance, authBalance);
        Money money = buildMoney(moneyAmount);

        balanceValidator.checkFreeAmount(operation, balance, money);
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