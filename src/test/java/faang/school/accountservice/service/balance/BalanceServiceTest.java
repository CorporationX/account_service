package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.AuthPayment;
import faang.school.accountservice.entity.AuthPaymentStatus;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.exception.non_retryable.EntityNotFoundException;
import faang.school.accountservice.repository.balance.AuthPaymentRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import faang.school.accountservice.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
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
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {
    private static final UUID BALANCE_ID = UUID.fromString("bac81744-4cfb-4ad9-abb6-7287dcf60452");
    private static final UUID AUTH_PAYMENT_ID = UUID.fromString("aac81744-4cfb-4ad9-abb6-7287dcf60451");
    private static final Long ACCOUNT_ID = 1L;

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private BalanceValidator balanceValidator;
    @Mock
    private AccountService accountService;

    @Mock
    private AuthPaymentRepository authPaymentRepository;

    @InjectMocks
    private BalanceService balanceService;

    private Balance balance;
    private AuthPayment authPayment;

    @BeforeEach
    void setUp() {
        balance = Balance.builder()
                .id(BALANCE_ID)
                .authBalance(BigDecimal.ZERO)
                .currentBalance(BigDecimal.valueOf(1000))
                .build();

        authPayment = AuthPayment.builder()
                .id(AUTH_PAYMENT_ID)
                .balance(balance)
                .amount(BigDecimal.valueOf(100))
                .build();
    }

    @Test
    @DisplayName("Create balance successful")
    void testCreateBalanceForAccountSuccessful() {
        Account account = Account.builder()
                .build();
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(account);

        balanceService.createBalanceForAccount(ACCOUNT_ID);

        verify(balanceRepository).save(any(Balance.class));
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
        when(balanceRepository.findById(BALANCE_ID)).thenReturn(of(balance));

        balanceService.authorizePayment(BALANCE_ID, money);

        ArgumentCaptor<Balance> balanceCaptor = ArgumentCaptor.forClass(Balance.class);
        ArgumentCaptor<AuthPayment> paymentCaptor = ArgumentCaptor.forClass(AuthPayment.class);

        verify(balanceRepository).save(balanceCaptor.capture());
        verify(authPaymentRepository).save(paymentCaptor.capture());

        Balance resultBalance = balanceCaptor.getValue();
        assertThat(resultBalance.getAuthBalance().doubleValue())
                .isEqualTo(authBalance + moneyAmount);

        AuthPayment resultPayment = paymentCaptor.getValue();
        assertThat(resultPayment.getAmount().doubleValue())
                .isNotNull()
                .isEqualTo(moneyAmount);
    }

    @Test
    @DisplayName("Reject payment successful")
    void testRejectPaymentSuccessful() {
        double authBalance = 7.0;
        double paymentAmount = 3.0;
        double currentBalance = 7.0;
        Balance balance = buildBalance(BALANCE_ID, authBalance, currentBalance);
        AuthPayment payment = buildAuthPayment(AUTH_PAYMENT_ID, balance, paymentAmount);

        when(authPaymentRepository.findById(AUTH_PAYMENT_ID)).thenReturn(Optional.of(payment));

        balanceService.rejectPayment(AUTH_PAYMENT_ID);

        ArgumentCaptor<AuthPayment> paymentCaptor = ArgumentCaptor.forClass(AuthPayment.class);
        ArgumentCaptor<Balance> balanceCaptor = ArgumentCaptor.forClass(Balance.class);

        verify(balanceRepository).save(balanceCaptor.capture());
        verify(authPaymentRepository).save(paymentCaptor.capture());

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
        when(balanceRepository.findById(BALANCE_ID)).thenReturn(of(balance));

        balanceService.topUpCurrentBalance(BALANCE_ID, money);

        ArgumentCaptor<Balance> balanceCaptor = ArgumentCaptor.forClass(Balance.class);

        verify(balanceRepository).save(balanceCaptor.capture());

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
        when(balanceRepository.findById(BALANCE_ID)).thenReturn(of(balance));

        balanceService.multiplyCurrentBalance(BALANCE_ID, value);

        ArgumentCaptor<Balance> balanceCaptor = ArgumentCaptor.forClass(Balance.class);

        verify(balanceRepository).save(balanceCaptor.capture());

        Balance resultBalance = balanceCaptor.getValue();
        assertThat(resultBalance.getCurrentBalance().doubleValue())
                .isEqualTo(currentBalance + currentBalance * value);
    }

    @Test
    @DisplayName("No auth payment by id and throw exception")
    void testFindAuthPaymentBiIdThrowException() {
        when(authPaymentRepository.findById(AUTH_PAYMENT_ID)).thenReturn(empty());

        assertThatThrownBy(() -> balanceService.findAuthPaymentById(AUTH_PAYMENT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Authorization payment with id:", AUTH_PAYMENT_ID);
    }

    @Test
    @DisplayName("Find auth payment by id successful")
    void testFindAuthPaymentBiIdSuccessful() {
        AuthPayment payment = buildAuthPayment(AUTH_PAYMENT_ID);
        when(authPaymentRepository.findById(AUTH_PAYMENT_ID)).thenReturn(of(payment));

        assertThat(balanceService.findAuthPaymentById(AUTH_PAYMENT_ID))
                .isEqualTo(payment);
    }

    @Test
    @DisplayName("No balance by id and throw exception")
    void testFindByIdThrowException() {
        when(balanceRepository.findById(BALANCE_ID)).thenReturn(empty());

        assertThatThrownBy(() -> balanceService.findById(BALANCE_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Balance by id : %s not found", BALANCE_ID);
    }

    @Test
    @DisplayName("Find balance by id successful")
    void testFindBiIdSuccessful() {
        Balance balance = buildBalance(BALANCE_ID);
        when(balanceRepository.findById(BALANCE_ID)).thenReturn(of(balance));

        assertThat(balanceService.findById(BALANCE_ID))
                .isEqualTo(balance);
    }

    @Test
    void testCheckBalanceFindByIdPositive() {
        when(balanceRepository.findById(BALANCE_ID)).thenReturn(Optional.of(balance));

        BigDecimal result = balanceService.checkBalanceFindById(BALANCE_ID);

        assertEquals(BigDecimal.valueOf(1000), result);
    }

    @Test
    void testCheckBalanceFindByIdThrowsResourceNotFoundException() {
        when(balanceRepository.findById(BALANCE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> balanceService.checkBalanceFindById(BALANCE_ID));
    }

    @Test
    void testFreezeFundsForTransferPositive() {
        Balance updatedBalance = Balance.builder()
                .id(BALANCE_ID)
                .authBalance(BigDecimal.valueOf(100))
                .currentBalance(BigDecimal.valueOf(900))
                .build();
        Money money = new Money(BigDecimal.valueOf(100), null);
        when(balanceRepository.findByIdForUpdateOrThrow(BALANCE_ID)).thenReturn(balance);
        when(balanceRepository.save(balance)).thenReturn(updatedBalance);
        when(authPaymentRepository.save(any(AuthPayment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthPayment result = balanceService.freezeFundsForTransfer(BALANCE_ID, money);

        assertEquals(money.amount(), result.getAmount());
        assertEquals(balance, result.getBalance());
        verify(balanceValidator).checkFreeAmount(balance, money);
        verify(balanceRepository).save(balance);
    }

    @Test
    void testFreezeFundsForTransferThrowsEntityNotFoundException() {
        Money money = new Money(BigDecimal.valueOf(100), null);
        when(balanceRepository.findByIdForUpdateOrThrow(BALANCE_ID)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> balanceService.freezeFundsForTransfer(BALANCE_ID, money));
    }

    @Test
    void testUnfreezeFundsForTransferPositive() {
        balance.setAuthBalance(BigDecimal.valueOf(100));

        when(balanceRepository.findByIdForUpdateOrThrow(BALANCE_ID)).thenReturn(balance);
        when(authPaymentRepository.findByIdForUpdateOrThrow(AUTH_PAYMENT_ID)).thenReturn(authPayment);

        balanceService.unfreezeFundsForTransfer(BALANCE_ID, AUTH_PAYMENT_ID);

        assertEquals(BigDecimal.ZERO, balance.getAuthBalance());
        assertEquals(BigDecimal.valueOf(1100), balance.getCurrentBalance());
        verify(authPaymentRepository).save(authPayment);
    }

    @Test
    void testUnfreezeFundsForTransferThrowsEntityNotFoundException() {
        when(balanceRepository.findByIdForUpdateOrThrow(BALANCE_ID)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> balanceService.unfreezeFundsForTransfer(BALANCE_ID, AUTH_PAYMENT_ID));
    }

    @Test
    void testFinalizeTransferPositive() {
        balance.setAuthBalance(BigDecimal.valueOf(100));
        when(balanceRepository.findByIdForUpdateOrThrow(BALANCE_ID)).thenReturn(balance);
        when(authPaymentRepository.findByIdForUpdateOrThrow(AUTH_PAYMENT_ID)).thenReturn(authPayment);

        balanceService.finalizeTransfer(BALANCE_ID, AUTH_PAYMENT_ID);

        assertEquals(BigDecimal.ZERO, balance.getAuthBalance());
        assertEquals(AuthPaymentStatus.CLOSED, authPayment.getStatus());
        verify(balanceRepository).save(balance);
        verify(authPaymentRepository).save(authPayment);
    }

    @Test
    void testFinalizeTransferThrowsEntityNotFoundException() {
        when(balanceRepository.findByIdForUpdateOrThrow(BALANCE_ID)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> balanceService.finalizeTransfer(BALANCE_ID, AUTH_PAYMENT_ID));
    }

    @Test
    void testIncreaseCurrentBalancePessimisticPositive() {
        when(balanceRepository.findByIdForUpdateOrThrow(BALANCE_ID)).thenReturn(balance);

        balanceService.increaseCurrentBalancePessimistic(BALANCE_ID, BigDecimal.valueOf(500));

        assertEquals(BigDecimal.valueOf(1500), balance.getCurrentBalance());
        verify(balanceRepository).save(balance);
    }

    @Test
    void testIncreaseCurrentBalancePessimisticThrowsEntityNotFoundException() {
        when(balanceRepository.findByIdForUpdateOrThrow(BALANCE_ID)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> balanceService.increaseCurrentBalancePessimistic(BALANCE_ID, BigDecimal.valueOf(500)));
    }
}