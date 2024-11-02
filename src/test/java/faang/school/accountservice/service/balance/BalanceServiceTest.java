package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.dto.listener.pending.OperationMessage;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.auth.payment.AuthPayment;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.repository.balance.AuthPaymentRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static faang.school.accountservice.enums.auth.payment.AuthPaymentStatus.CLOSED;
import static faang.school.accountservice.enums.auth.payment.AuthPaymentStatus.REJECTED;
import static faang.school.accountservice.enums.pending.Category.OTHER;
import static faang.school.accountservice.util.fabrics.AccountFabric.buildAccount;
import static faang.school.accountservice.util.fabrics.AuthPaymentFabric.buildAuthPayment;
import static faang.school.accountservice.util.fabrics.BalanceFabric.buildBalance;
import static faang.school.accountservice.util.fabrics.MoneyFabric.buildMoney;
import static faang.school.accountservice.util.fabrics.OperationMessageFabric.buildOperationMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {
    private static final UUID ACCOUNT_ID = UUID.randomUUID();
    private static final UUID SOURCE_BALANCE_ID = UUID.randomUUID();
    private static final UUID TARGET_BALANCE_ID = UUID.randomUUID();
    private static final UUID BALANCE_ID = UUID.randomUUID();
    private static final UUID PENDING_ID = UUID.randomUUID();
    private static final UUID AUTH_PAYMENT_ID = UUID.randomUUID();

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private AuthPaymentRepository authPaymentRepository;

    @Mock
    private BalanceValidator balanceValidator;

    @InjectMocks
    private BalanceService balanceService;

    @Test
    void testCreateBalance_successful() {
        Account account = buildAccount();
        balanceService.createBalance(account);

        verify(balanceRepository).saveAndFlush(any(Balance.class));
    }

    @Test
    void testAuthorizePayment_successful() {
        double moneyAmount = 1.0;
        double authBalance = 1.0;
        double currentBalance = 1.0;
        Balance sourceBalance = buildBalance(SOURCE_BALANCE_ID, authBalance, currentBalance);
        Balance targetBalance = buildBalance(TARGET_BALANCE_ID, authBalance, currentBalance);
        Money money = buildMoney(moneyAmount);
        OperationMessage operation = buildOperationMessage(PENDING_ID, ACCOUNT_ID);

        balanceService.authorizePayment(operation, sourceBalance, targetBalance, money, OTHER);

        verify(balanceValidator).checkFreeAmount(operation, sourceBalance, money);

        ArgumentCaptor<Balance> sourceBalanceCaptor = ArgumentCaptor.forClass(Balance.class);
        ArgumentCaptor<AuthPayment> paymentCaptor = ArgumentCaptor.forClass(AuthPayment.class);

        verify(balanceRepository).saveAndFlush(sourceBalanceCaptor.capture());
        verify(authPaymentRepository).saveAndFlush(paymentCaptor.capture());

        Balance resultBalance = sourceBalanceCaptor.getValue();
        assertThat(resultBalance.getAuthBalance().doubleValue())
                .isEqualTo(authBalance + moneyAmount);

        AuthPayment resultPayment = paymentCaptor.getValue();
        assertThat(resultPayment.getSourceBalance())
                .isNotNull()
                .isEqualTo(resultBalance);
        assertThat(resultPayment.getTargetBalance())
                .isNotNull()
                .isEqualTo(targetBalance);
        assertThat(resultPayment.getAmount().doubleValue())
                .isEqualTo(moneyAmount);
    }

    @Test
    @DisplayName("Accept payment successful")
    void testAcceptPayment_successful() {
        double moneyAmount = 2.0;
        double authBalance = 2.0;
        double paymentAmount = 2.0;
        double currentBalance = 2.0;
        Balance sourceBalance = buildBalance(SOURCE_BALANCE_ID, authBalance, currentBalance);
        Balance targetBalance = buildBalance(TARGET_BALANCE_ID, authBalance, currentBalance);
        AuthPayment payment = buildAuthPayment(AUTH_PAYMENT_ID, sourceBalance, targetBalance, paymentAmount);
        Money money = buildMoney(moneyAmount);
        when(authPaymentRepository.findById(AUTH_PAYMENT_ID)).thenReturn(Optional.of(payment));

        balanceService.acceptPayment(AUTH_PAYMENT_ID, money);

        verify(balanceValidator).checkAuthPaymentForAccept(money, payment);

        ArgumentCaptor<Balance> sourceBalanceCaptor = ArgumentCaptor.forClass(Balance.class);
        ArgumentCaptor<AuthPayment> paymentCaptor = ArgumentCaptor.forClass(AuthPayment.class);

        verify(balanceRepository, times(2)).saveAndFlush(sourceBalanceCaptor.capture());
        verify(authPaymentRepository).saveAndFlush(paymentCaptor.capture());

        Balance resultSourceBalance = sourceBalanceCaptor.getAllValues().get(0);
        assertThat(resultSourceBalance.getCurrentBalance().doubleValue())
                .isEqualTo(currentBalance - moneyAmount);
        assertThat(resultSourceBalance.getAuthBalance().doubleValue())
                .isEqualTo(authBalance - paymentAmount);

        Balance resultTargetBalance = sourceBalanceCaptor.getAllValues().get(1);
        assertThat(resultTargetBalance.getCurrentBalance().doubleValue())
                .isEqualTo(currentBalance + moneyAmount);
        assertThat(resultTargetBalance.getAuthBalance().doubleValue())
                .isEqualTo(authBalance);

        AuthPayment paymentResult = paymentCaptor.getValue();
        assertThat(paymentResult.getAmount().doubleValue())
                .isEqualTo(moneyAmount);
        assertThat(paymentResult.getStatus())
                .isNotNull()
                .isEqualTo(CLOSED);
    }

    @Test
    void testRejectPayment_successful() {
        double authBalance = 5.0;
        double paymentAmount = 2.0;
        double currentBalance = 5.0;
        Balance sourceBalance = buildBalance(SOURCE_BALANCE_ID, authBalance, currentBalance);
        AuthPayment payment = buildAuthPayment(AUTH_PAYMENT_ID, sourceBalance, paymentAmount);
        when(authPaymentRepository.findById(AUTH_PAYMENT_ID)).thenReturn(Optional.of(payment));

        balanceService.rejectPayment(AUTH_PAYMENT_ID);

        verify(balanceValidator).checkAuthPaymentForReject(payment);

        ArgumentCaptor<AuthPayment> paymentCaptor = ArgumentCaptor.forClass(AuthPayment.class);
        ArgumentCaptor<Balance> sourceBalanceCaptor = ArgumentCaptor.forClass(Balance.class);

        verify(balanceRepository).saveAndFlush(sourceBalanceCaptor.capture());
        verify(authPaymentRepository).saveAndFlush(paymentCaptor.capture());

        AuthPayment resultPayment = paymentCaptor.getValue();
        assertThat(resultPayment.getStatus())
                .isNotNull()
                .isEqualTo(REJECTED);

        Balance resultBalance = sourceBalanceCaptor.getValue();
        assertThat(resultBalance.getAuthBalance().doubleValue())
                .isEqualTo(authBalance - paymentAmount);
    }

    @Test
    void testTopUpCurrentBalance_successful() {
        double moneyAmount = 5.0;
        double authBalance = 5.0;
        double currentBalance = 5.0;
        Balance balance = buildBalance(BALANCE_ID, authBalance, currentBalance);
        Money money = buildMoney(moneyAmount);
        when(balanceRepository.findById(BALANCE_ID)).thenReturn(Optional.of(balance));

        balanceService.topUpCurrentBalance(BALANCE_ID, money);

        ArgumentCaptor<Balance> balanceCaptor = ArgumentCaptor.forClass(Balance.class);

        verify(balanceRepository).saveAndFlush(balanceCaptor.capture());

        Balance resultBalance = balanceCaptor.getValue();
        assertThat(resultBalance.getCurrentBalance().doubleValue())
                .isEqualTo(currentBalance + moneyAmount);
    }

    @Test
    void testMultiplyCurrentBalance_successful() {
        double value = 2.0;
        double authBalance = 5.0;
        double currentBalance = 5.0;
        Balance balance = buildBalance(BALANCE_ID, authBalance, currentBalance);
        when(balanceRepository.findById(BALANCE_ID)).thenReturn(Optional.of(balance));

        balanceService.multiplyCurrentBalance(BALANCE_ID, value);

        ArgumentCaptor<Balance> balanceCaptor = ArgumentCaptor.forClass(Balance.class);

        verify(balanceRepository).saveAndFlush(balanceCaptor.capture());

        Balance resultBalance = balanceCaptor.getValue();
        assertThat(resultBalance.getCurrentBalance().doubleValue())
                .isEqualTo(currentBalance + currentBalance * value);
    }

    @Test
    void testFindById_throwException() {
        when(balanceRepository.findById(BALANCE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> balanceService.findById(BALANCE_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("%s id=%s not found", Balance.class.getName(), BALANCE_ID);
    }

    @Test
    void testFindBiId_successful() {
        Balance balance = buildBalance(BALANCE_ID);
        when(balanceRepository.findById(BALANCE_ID)).thenReturn(Optional.of(balance));

        assertThat(balanceService.findById(BALANCE_ID))
                .isEqualTo(balance);
    }

    @Test
    void testFindAuthPaymentBiId_throwException() {
        when(authPaymentRepository.findById(AUTH_PAYMENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> balanceService.findAuthPaymentBiId(AUTH_PAYMENT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("%s id=%s not found", AuthPayment.class.getName(), AUTH_PAYMENT_ID);
    }

    @Test
    void testFindAuthPaymentBiId_successful() {
        AuthPayment payment = buildAuthPayment(AUTH_PAYMENT_ID);
        when(authPaymentRepository.findById(AUTH_PAYMENT_ID)).thenReturn(Optional.of(payment));

        assertThat(balanceService.findAuthPaymentBiId(AUTH_PAYMENT_ID))
                .isEqualTo(payment);
    }

    @Test
    void testFindByAccountId_throwException() {
        when(balanceRepository.findBalanceByAccountId(ACCOUNT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> balanceService.findByAccountId(ACCOUNT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("%s id=%s not found", Account.class.getName(), ACCOUNT_ID);
    }

    @Test
    void testFindBiAccountId_successful() {
        Balance balance = buildBalance(BALANCE_ID);
        when(balanceRepository.findBalanceByAccountId(ACCOUNT_ID)).thenReturn(Optional.of(balance));

        assertThat(balanceService.findByAccountId(ACCOUNT_ID))
                .isEqualTo(balance);
    }
}
