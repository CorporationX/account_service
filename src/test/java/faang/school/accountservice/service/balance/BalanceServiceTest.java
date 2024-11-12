package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.dto.payment.request.AuthPaymentRequest;
import faang.school.accountservice.dto.payment.request.CancelPaymentRequest;
import faang.school.accountservice.dto.payment.request.ClearingPaymentRequest;
import faang.school.accountservice.dto.payment.request.ErrorPaymentRequest;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.auth.payment.AuthPayment;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.repository.balance.AuthPaymentRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static faang.school.accountservice.enums.payment.Currency.USD;
import static faang.school.accountservice.enums.auth.payment.AuthPaymentStatus.ACTIVE;
import static faang.school.accountservice.enums.auth.payment.AuthPaymentStatus.CLOSED;
import static faang.school.accountservice.enums.auth.payment.AuthPaymentStatus.REJECTED;
import static faang.school.accountservice.enums.payment.Category.OTHER;
import static faang.school.accountservice.util.fabrics.AccountFabric.buildAccount;
import static faang.school.accountservice.util.fabrics.AuthPaymentFabric.buildAuthPayment;
import static faang.school.accountservice.util.fabrics.BalanceFabric.buildBalance;
import static faang.school.accountservice.util.fabrics.MoneyFabric.buildMoney;
import static faang.school.accountservice.util.fabrics.PaymentsFabric.buildAuthPaymentRequest;
import static faang.school.accountservice.util.fabrics.PaymentsFabric.buildCancelPaymentRequest;
import static faang.school.accountservice.util.fabrics.PaymentsFabric.buildClearingPaymentRequest;
import static faang.school.accountservice.util.fabrics.PaymentsFabric.buildErrorPaymentRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {
    private static final UUID ACCOUNT_ID = UUID.randomUUID();
    private static final UUID SOURCE_ACCOUNT_ID = UUID.randomUUID();
    private static final UUID TARGET_ACCOUNT_ID = UUID.randomUUID();
    private static final UUID OPERATION_ID = UUID.randomUUID();
    private static final UUID SOURCE_BALANCE_ID = UUID.randomUUID();
    private static final UUID TARGET_BALANCE_ID = UUID.randomUUID();
    private static final UUID BALANCE_ID = UUID.randomUUID();
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
        double sourceAuthBalance = 0.0;
        double sourceTargetBalance = 1.0;
        double targetAuthBalance = 0;
        double targetCurrentBalance = 0;
        Balance sourceBalance = buildBalance(SOURCE_BALANCE_ID, sourceAuthBalance, sourceTargetBalance);
        Balance targetBalance = buildBalance(TARGET_BALANCE_ID, targetAuthBalance, targetCurrentBalance);
        Money money = new Money(BigDecimal.valueOf(moneyAmount), USD);
        AuthPaymentRequest authPaymentRequest = buildAuthPaymentRequest(OPERATION_ID, SOURCE_ACCOUNT_ID,
                TARGET_ACCOUNT_ID, BigDecimal.valueOf(moneyAmount), USD, OTHER);

        when(balanceRepository.findBalanceByAccountId(SOURCE_ACCOUNT_ID)).thenReturn(Optional.of(sourceBalance));
        when(balanceRepository.findBalanceByAccountId(TARGET_ACCOUNT_ID)).thenReturn(Optional.of(targetBalance));

        balanceService.authorizePayment(authPaymentRequest);

        ArgumentCaptor<UUID> requestUuidCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Balance> sourceBalanceForValidateCaptor = ArgumentCaptor.forClass(Balance.class);
        ArgumentCaptor<Money> moneyCaptor = ArgumentCaptor.forClass(Money.class);

        verify(balanceValidator).checkFreeAmount(requestUuidCaptor.capture(), sourceBalanceForValidateCaptor.capture(),
                moneyCaptor.capture());

        ArgumentCaptor<Balance> sourceBalanceCaptor = ArgumentCaptor.forClass(Balance.class);
        ArgumentCaptor<AuthPayment> paymentCaptor = ArgumentCaptor.forClass(AuthPayment.class);

        verify(balanceRepository).saveAndFlush(sourceBalanceCaptor.capture());
        verify(authPaymentRepository).saveAndFlush(paymentCaptor.capture());

        assertThat(requestUuidCaptor.getValue())
                .isEqualTo(OPERATION_ID);
        assertThat(sourceBalanceForValidateCaptor.getValue())
                .isEqualTo(sourceBalance);
        assertThat(moneyCaptor.getValue())
                .isEqualTo(money);

        Balance resultSourceBalance = sourceBalanceCaptor.getValue();
        assertThat(resultSourceBalance.getAuthBalance().doubleValue())
                .isEqualTo(sourceAuthBalance + moneyAmount);
        assertThat(resultSourceBalance.getCurrentBalance().doubleValue())
                .isEqualTo(sourceTargetBalance - moneyAmount);

        AuthPayment resultPayment = paymentCaptor.getValue();
        assertThat(resultPayment.getSourceBalance())
                .isEqualTo(sourceBalance);
        assertThat(resultPayment.getTargetBalance())
                .isEqualTo(targetBalance);
        assertThat(resultPayment.getAmount().doubleValue())
                .isEqualTo(moneyAmount);
        assertThat(resultPayment.getStatus())
                .isEqualTo(ACTIVE);
    }

    @Test
    void testClearingPayment_successful() {
        double paymentAmount = 2.0;
        double moneyAmount = 2.0;
        double sourceAuthBalance = 2.0;
        double sourceCurrentBalance = 2.0;
        double targetAuthBalance = 0;
        double targetCurrentBalance = 0;
        Balance sourceBalance = buildBalance(SOURCE_BALANCE_ID, sourceAuthBalance, sourceCurrentBalance);
        Balance targetBalance = buildBalance(TARGET_BALANCE_ID, targetAuthBalance, targetCurrentBalance);
        AuthPayment payment = buildAuthPayment(AUTH_PAYMENT_ID, sourceBalance, targetBalance, paymentAmount);
        ClearingPaymentRequest clearingPaymentRequest = buildClearingPaymentRequest(OPERATION_ID);

        when(authPaymentRepository.findById(OPERATION_ID)).thenReturn(Optional.of(payment));

        balanceService.clearingPayment(clearingPaymentRequest);

        ArgumentCaptor<AuthPayment> authPaymentForForValidCaptor = ArgumentCaptor.forClass(AuthPayment.class);

        verify(balanceValidator).checkAuthPaymentForAccept(authPaymentForForValidCaptor.capture());

        ArgumentCaptor<Balance> balancesCaptor = ArgumentCaptor.forClass(Balance.class);
        ArgumentCaptor<AuthPayment> paymentCaptor = ArgumentCaptor.forClass(AuthPayment.class);

        verify(balanceRepository, times(2)).saveAndFlush(balancesCaptor.capture());
        verify(authPaymentRepository).saveAndFlush(paymentCaptor.capture());

        assertThat(authPaymentForForValidCaptor.getValue()).isEqualTo(payment);

        Balance resultSourceBalance = balancesCaptor.getAllValues().get(0);
        assertThat(resultSourceBalance.getAuthBalance().doubleValue())
                .isEqualTo(sourceAuthBalance - moneyAmount);
        assertThat(resultSourceBalance.getCurrentBalance().doubleValue())
                .isEqualTo(sourceCurrentBalance);

        Balance resultTargetBalance = balancesCaptor.getAllValues().get(1);
        assertThat(resultTargetBalance.getAuthBalance().doubleValue())
                .isEqualTo(targetAuthBalance);
        assertThat(resultTargetBalance.getCurrentBalance().doubleValue())
                .isEqualTo(targetCurrentBalance + moneyAmount);

        AuthPayment paymentResult = paymentCaptor.getValue();
        assertThat(paymentResult.getAmount().doubleValue())
                .isEqualTo(moneyAmount);
        assertThat(paymentResult.getStatus())
                .isNotNull()
                .isEqualTo(CLOSED);
    }

    @Test
    void testCancelPayment_successful() {
        double paymentAmount = 2.0;
        double sourceAuthBalance = 2.0;
        double sourceCurrentBalance = 2.0;
        Balance sourceBalance = buildBalance(SOURCE_BALANCE_ID, sourceAuthBalance, sourceCurrentBalance);
        AuthPayment payment = buildAuthPayment(AUTH_PAYMENT_ID, sourceBalance, paymentAmount);
        CancelPaymentRequest cancelPaymentRequest = buildCancelPaymentRequest(OPERATION_ID);

        when(authPaymentRepository.findById(OPERATION_ID)).thenReturn(Optional.of(payment));

        balanceService.cancelPayment(cancelPaymentRequest);

        ArgumentCaptor<AuthPayment> authPaymentForForValidCaptor = ArgumentCaptor.forClass(AuthPayment.class);

        verify(balanceValidator).checkAuthPaymentForReject(authPaymentForForValidCaptor.capture());

        ArgumentCaptor<Balance> sourceBalanceCaptor = ArgumentCaptor.forClass(Balance.class);
        ArgumentCaptor<AuthPayment> paymentCaptor = ArgumentCaptor.forClass(AuthPayment.class);

        verify(balanceRepository).saveAndFlush(sourceBalanceCaptor.capture());
        verify(authPaymentRepository).saveAndFlush(paymentCaptor.capture());

        Balance resultSourceBalance = sourceBalanceCaptor.getValue();
        assertThat(resultSourceBalance.getAuthBalance().doubleValue())
                .isEqualTo(sourceAuthBalance - paymentAmount);
        assertThat(resultSourceBalance.getCurrentBalance().doubleValue())
                .isEqualTo(sourceCurrentBalance + paymentAmount);

        AuthPayment resultPayment = paymentCaptor.getValue();
        assertThat(resultPayment.getStatus())
                .isEqualTo(REJECTED);
    }

    @Test
    void testErrorPayment_successful() {
        double paymentAmount = 2.0;
        double sourceAuthBalance = 2.0;
        double sourceCurrentBalance = 2.0;
        Balance sourceBalance = buildBalance(SOURCE_BALANCE_ID, sourceAuthBalance, sourceCurrentBalance);
        AuthPayment payment = buildAuthPayment(AUTH_PAYMENT_ID, sourceBalance, paymentAmount);
        ErrorPaymentRequest errorPaymentRequest = buildErrorPaymentRequest(OPERATION_ID);

        when(authPaymentRepository.findById(OPERATION_ID)).thenReturn(Optional.of(payment));

        balanceService.errorPayment(errorPaymentRequest);

        ArgumentCaptor<AuthPayment> authPaymentForForValidCaptor = ArgumentCaptor.forClass(AuthPayment.class);

        verify(balanceValidator).checkAuthPaymentForReject(authPaymentForForValidCaptor.capture());

        ArgumentCaptor<Balance> sourceBalanceCaptor = ArgumentCaptor.forClass(Balance.class);
        ArgumentCaptor<AuthPayment> paymentCaptor = ArgumentCaptor.forClass(AuthPayment.class);

        verify(balanceRepository).saveAndFlush(sourceBalanceCaptor.capture());
        verify(authPaymentRepository).saveAndFlush(paymentCaptor.capture());

        Balance resultSourceBalance = sourceBalanceCaptor.getValue();
        assertThat(resultSourceBalance.getAuthBalance().doubleValue())
                .isEqualTo(sourceAuthBalance - paymentAmount);
        assertThat(resultSourceBalance.getCurrentBalance().doubleValue())
                .isEqualTo(sourceCurrentBalance + paymentAmount);

        AuthPayment resultPayment = paymentCaptor.getValue();
        assertThat(resultPayment.getStatus())
                .isEqualTo(REJECTED);
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
        when(balanceRepository.findByAccount_IdWithLock(ACCOUNT_ID)).thenReturn(Optional.of(balance));

        balanceService.multiplyCurrentBalance(ACCOUNT_ID, value);

        ArgumentCaptor<Balance> balanceCaptor = ArgumentCaptor.forClass(Balance.class);

        verify(balanceRepository).save(balanceCaptor.capture());

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
                .hasMessageContaining("%s id=%s not found", Balance.class.getName(), ACCOUNT_ID);
    }

    @Test
    void testFindBiAccountId_successful() {
        Balance balance = buildBalance(BALANCE_ID);
        when(balanceRepository.findBalanceByAccountId(ACCOUNT_ID)).thenReturn(Optional.of(balance));

        assertThat(balanceService.findByAccountId(ACCOUNT_ID))
                .isEqualTo(balance);
    }
}
