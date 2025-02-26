package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.AuthPayment;
import faang.school.accountservice.model.AuthPaymentStatus;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.balance.AuthPaymentRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static faang.school.accountservice.util.AuthPaymentFabrics.buildAuthPayment;
import static faang.school.accountservice.util.BalanceFabrics.buildBalance;
import static faang.school.accountservice.util.ManyFabrics.buildMoney;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

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
    @DisplayName("Create balance successful")
    void testCreateBalanceSuccessful() {
        Account account = Account.builder()
                .build();

        balanceService.createBalance(account);

        Mockito.verify(balanceRepository).save(Mockito.any(Balance.class));
    }

    @Test
    @DisplayName("Authorize payment successful")
    void testAuthorizePaymentSuccessful() {
        double moneyAmount = 1.0;
        double authBalance = 1.0;
        double currentBalance = 1.0;
        Balance balance = Balance.builder()
                .id(BALANCE_ID)
                .authBalance(BigDecimal.valueOf(authBalance))
                .currentBalance(BigDecimal.valueOf(currentBalance))
                .build();

        Money money = buildMoney(moneyAmount);
        when(balanceRepository.findById(BALANCE_ID)).thenReturn(Optional.of(balance));

        balanceService.authorizePayment(BALANCE_ID, money);

        ArgumentCaptor<Balance> balanceCaptor = ArgumentCaptor.forClass(Balance.class);
        ArgumentCaptor<AuthPayment> paymentCaptor = ArgumentCaptor.forClass(AuthPayment.class);

        Mockito.verify(balanceRepository).save(balanceCaptor.capture());
        Mockito.verify(authPaymentRepository).save(paymentCaptor.capture());

        Balance resultBalance = balanceCaptor.getValue();
        assertThat(resultBalance.getAuthBalance().doubleValue())
                .isEqualTo(authBalance + moneyAmount);

        AuthPayment resultPayment = paymentCaptor.getValue();
        assertThat(resultPayment.getBalance())
                .isNotNull()
                .isEqualTo(resultBalance);
        assertThat(resultPayment.getAmount().doubleValue())
                .isEqualTo(moneyAmount);
    }

    @Test
    @DisplayName("Reject payment successful")
    void testRejectPaymentSuccessful() {
        double authBalance = 7.0;
        double paymentAmount = 3.0;
        double currentBalance = 7.0;
        Balance balance = buildBalance(BALANCE_ID, authBalance, currentBalance);
        AuthPayment payment = buildAuthPayment(AUTH_PAYMENT_ID, paymentAmount, balance);
        when(authPaymentRepository.findById(AUTH_PAYMENT_ID)).thenReturn(Optional.of(payment));

        balanceService.rejectPayment(AUTH_PAYMENT_ID);

        ArgumentCaptor<AuthPayment> paymentCaptor = ArgumentCaptor.forClass(AuthPayment.class);
        ArgumentCaptor<Balance> balanceCaptor = ArgumentCaptor.forClass(Balance.class);

        Mockito.verify(balanceRepository).save(balanceCaptor.capture());
        Mockito.verify(authPaymentRepository).save(paymentCaptor.capture());

        AuthPayment resultPayment = paymentCaptor.getValue();
        assertThat(resultPayment.getStatus())
                .isNotNull()
                .isEqualTo(AuthPaymentStatus.REJECTED);

        Balance resultBalance = balanceCaptor.getValue();
        assertThat(resultBalance.getAuthBalance().doubleValue())
                .isEqualTo(authBalance - paymentAmount);
    }

    @Test
    @DisplayName("Top up current balance successful")
    void testTopUpCurrentBalanceSuccessful() {
        double moneyAmount = 10.0;
        double authBalance = 10.0;
        double currentBalance = 10.0;
        Balance balance = buildBalance(BALANCE_ID, authBalance, currentBalance);
        Money money = buildMoney(moneyAmount);
        when(balanceRepository.findById(BALANCE_ID)).thenReturn(Optional.of(balance));

        balanceService.topUpCurrentBalance(BALANCE_ID, money);

        ArgumentCaptor<Balance> balanceCaptor = ArgumentCaptor.forClass(Balance.class);

        Mockito.verify(balanceRepository).save(balanceCaptor.capture());

        Balance resultBalance = balanceCaptor.getValue();
        assertThat(resultBalance.getCurrentBalance().doubleValue())
                .isEqualTo(currentBalance + moneyAmount);
    }

    @Test
    @DisplayName("Multiply current balance successful")
    void testMultiplyCurrentBalanceSuccessful() {
        double value = 2.0;
        double authBalance = 5.0;
        double currentBalance = 5.0;
        Balance balance = buildBalance(BALANCE_ID, authBalance, currentBalance);
        when(balanceRepository.findById(BALANCE_ID)).thenReturn(Optional.of(balance));

        balanceService.multiplyCurrentBalance(BALANCE_ID, value);

        ArgumentCaptor<Balance> balanceCaptor = ArgumentCaptor.forClass(Balance.class);

        Mockito.verify(balanceRepository).save(balanceCaptor.capture());

        Balance resultBalance = balanceCaptor.getValue();
        assertThat(resultBalance.getCurrentBalance().doubleValue())
                .isEqualTo(currentBalance + currentBalance * value);
    }

    @Test
    @DisplayName("No auth payment by id and throw exception")
    void testFindAuthPaymentBiIdThrowException() {
        when(authPaymentRepository.findById(AUTH_PAYMENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> balanceService.findAuthPaymentById(AUTH_PAYMENT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Authorization payment with id:", AUTH_PAYMENT_ID);
    }

    @Test
    @DisplayName("Find auth payment by id successful")
    void testFindAuthPaymentBiIdSuccessful() {
        AuthPayment payment = buildAuthPayment(AUTH_PAYMENT_ID);
        when(authPaymentRepository.findById(AUTH_PAYMENT_ID)).thenReturn(Optional.of(payment));

        assertThat(balanceService.findAuthPaymentById(AUTH_PAYMENT_ID))
                .isEqualTo(payment);
    }

    @Test
    @DisplayName("No balance by id and throw exception")
    void testFindByIdThrowException() {
        when(balanceRepository.findById(BALANCE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> balanceService.findById(BALANCE_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Balance by id : %s not found", BALANCE_ID);
    }

    @Test
    @DisplayName("Find balance by id successful")
    void testFindBiIdSuccessful() {
        Balance balance = buildBalance(BALANCE_ID);
        when(balanceRepository.findById(BALANCE_ID)).thenReturn(Optional.of(balance));

        assertThat(balanceService.findById(BALANCE_ID))
                .isEqualTo(balance);
    }
}