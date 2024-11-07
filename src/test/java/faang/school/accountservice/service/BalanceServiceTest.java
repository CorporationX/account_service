package faang.school.accountservice.service;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.balance.AuthorizationStatus;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.model.balance.BalanceAuthPayment;
import faang.school.accountservice.repository.BalanceAuthPaymentRepository;
import faang.school.accountservice.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {
    @InjectMocks
    private BalanceService balanceService;

    private final BalanceRepository balanceRepository = mock(BalanceRepository.class);
    private final BalanceAuthPaymentRepository balanceAuthPaymentRepository = mock(BalanceAuthPaymentRepository.class);

    private Balance balance;
    BalanceAuthPayment authPayment;

    @BeforeEach
    void setUp() {
        balance = Balance.builder()
                .id(UUID.randomUUID())
                .authorization(BigDecimal.valueOf(0))
                .actual(BigDecimal.valueOf(0))
                .build();

        authPayment = BalanceAuthPayment.builder()
                .id(UUID.randomUUID())
                .balance(balance)
                .amount(BigDecimal.valueOf(100))
                .status(AuthorizationStatus.ACTIVATED)
                .build();
    }

    @Test
    @DisplayName("Balance service: validate currency")
    void testCreateAuthPayment_checkCurrency() {
        Money money = new Money(BigDecimal.valueOf(100), Currency.EUR);

        assertThrows(RuntimeException.class, () -> balanceService.createAuthPayment(balance.getId(), money));
    }

    @Test
    @DisplayName("Balance service: validate positive sum")
    void testCreateAuthPayment_checkPositiveSum() {
        Money money = new Money(BigDecimal.valueOf(-100), Currency.RUB);

        assertThrows(RuntimeException.class, () -> balanceService.createAuthPayment(balance.getId(), money));
    }

    @Test
    @DisplayName("Balance service: validate balance exists")
    void testCreateAuthPayment_checkBalanceExists() {
        Money money = new Money(BigDecimal.valueOf(100), Currency.RUB);

        when(balanceRepository.findById(balance.getId())).thenThrow();
        assertThrows(RuntimeException.class, () -> balanceService.createAuthPayment(balance.getId(), money));
    }

    @Test
    @DisplayName("Balance service: validate enough money")
    void testCreateAuthPayment_checkEnoughMoney() {
        Money money = new Money(BigDecimal.valueOf(150), Currency.RUB);
        when(balanceRepository.findById(balance.getId())).thenReturn(Optional.of(balance));
        balance.setActual(BigDecimal.valueOf(100));

        assertThrows(RuntimeException.class, () -> balanceService.createAuthPayment(balance.getId(), money));
    }

    @Test
    @DisplayName("Balance service: check execute authorization payment")
    void testCreateAuthPayment_checkExecute() {
        Money money = new Money(BigDecimal.valueOf(75), Currency.RUB);
        when(balanceRepository.findById(balance.getId())).thenReturn(Optional.of(balance));
        balance.setActual(BigDecimal.valueOf(150));
        balance.setAuthorization(BigDecimal.valueOf(50));

        balanceService.createAuthPayment(balance.getId(), money);

        verify(balanceRepository).saveAndFlush(any(Balance.class));
        verify(balanceAuthPaymentRepository).saveAndFlush(any(BalanceAuthPayment.class));
    }

    @Test
    @DisplayName("Balance service: check exist UUID authorization payment")
    void testRejectAuthPayment_checkExistAuthPayment() {
        when(balanceAuthPaymentRepository.findById(authPayment.getId())).thenThrow();

        assertThrows(RuntimeException.class, () -> balanceService.rejectAuthPayment(authPayment.getId()));
    }

    @Test
    @DisplayName("Balance service: check execute reject authorization payment")
    void testRejectAuthPayment_checkExecute() {
        when(balanceAuthPaymentRepository.findById(authPayment.getId())).thenReturn(Optional.of(authPayment));

        balanceService.rejectAuthPayment(authPayment.getId());
        assertEquals(authPayment.getStatus(), AuthorizationStatus.REJECTED);

        verify(balanceRepository).saveAndFlush(any(Balance.class));
        verify(balanceAuthPaymentRepository).saveAndFlush(any(BalanceAuthPayment.class));
    }

    @Test
    @DisplayName("Balance service: check execute top up balance")
    void testTopUpCurrentBalance_checkExecute() {
        when(balanceRepository.findById(balance.getId())).thenReturn(Optional.of(balance));
        Money money = new Money(BigDecimal.valueOf(100), Currency.RUB);

        when(balanceRepository.saveAndFlush(balance)).thenReturn(balance);
        Balance checkedBalance = balanceService.topUpCurrentBalance(balance.getId(), money);

        assertEquals(balance.getActual(), money.amount());
        assertNotNull(checkedBalance);
        verify(balanceRepository).saveAndFlush(any(Balance.class));
    }

    @Test
    @DisplayName("Balance service: validate payment status")
    void testCompleteAuthorizationWriteOff_checkPaymentStatus() {
        when(balanceAuthPaymentRepository.findById(authPayment.getId())).thenReturn(Optional.of(authPayment));
        authPayment.setStatus(AuthorizationStatus.REJECTED);
        assertThrows(RuntimeException.class, () -> balanceService.completeAuthorizationWriteOff(authPayment.getId()));
    }

    @Test
    @DisplayName("Balance service: validate actual balance")
    void testCompleteAuthorizationWriteOff_checkActualBalance() {
        when(balanceAuthPaymentRepository.findById(authPayment.getId())).thenReturn(Optional.of(authPayment));
        authPayment.setStatus(AuthorizationStatus.ACTIVATED);
        authPayment.setBalance(balance);
        authPayment.setAmount(BigDecimal.valueOf(100));

        assertThrows(RuntimeException.class, () -> balanceService.completeAuthorizationWriteOff(authPayment.getId()));
    }

    @Test
    @DisplayName("Balance service: check execute")
    void testCompleteAuthorizationWriteOff_checkExecute() {
        when(balanceAuthPaymentRepository.findById(authPayment.getId())).thenReturn(Optional.of(authPayment));
        authPayment.setStatus(AuthorizationStatus.ACTIVATED);
        balance.setActual(BigDecimal.valueOf(50));
        balance.setAuthorization(BigDecimal.valueOf(100));
        authPayment.setBalance(balance);
        authPayment.setAmount(BigDecimal.valueOf(100));

        balanceService.completeAuthorizationWriteOff(authPayment.getId());

        assertEquals(authPayment.getBalance().getAuthorization(), BigDecimal.valueOf(0));
        assertEquals(authPayment.getBalance().getActual(), BigDecimal.valueOf(50));
        assertEquals(authPayment.getStatus(), AuthorizationStatus.CONFIRMED);

        verify(balanceRepository).saveAndFlush(any(Balance.class));
        verify(balanceAuthPaymentRepository).saveAndFlush(any(BalanceAuthPayment.class));
    }
}
