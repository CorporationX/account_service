package faang.school.accountservice.service.balance;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.mapper.BalanceMapperImpl;
import faang.school.accountservice.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BalanceServiceImplTest {

    @Mock
    private BalanceRepository balanceRepositoryMock;
    @Spy
    private BalanceMapperImpl balanceMapper;
    @InjectMocks
    private BalanceServiceImpl balanceService;

    private Balance zeroBalance;
    private Balance positiveBalance;
    private Balance heldFundsBalance;

    private final BigDecimal positiveBalanceAmount = new BigDecimal("1000.00");
    private final BigDecimal heldAmount = new BigDecimal("151.98");

    @Captor
    private ArgumentCaptor<Balance> balanceCaptor;

    @BeforeEach
    void setUp() {
        Account account = Account.builder()
                .id(1L)
                .account("123123123")
                .accountType(AccountType.PAYMENT_ACCOUNT)
                .build();
        zeroBalance = Balance.builder()
                .id(1L)
                .account(account)
                .build();

        positiveBalance = Balance.builder()
                .id(1L)
                .account(account)
                .actualBalance(positiveBalanceAmount)
                .build();

        heldFundsBalance = Balance.builder()
                .id(1L)
                .account(account)
                .actualBalance(positiveBalanceAmount.subtract(heldAmount))
                .authorizedBalance(heldAmount)
                .build();
    }

    @Test
    @DisplayName("Test top-up balance")
    void testTopUpBalance() {
        Long balanceId = 1L;
        Balance capturedBalance;
        BigDecimal amount = new BigDecimal("10.11");
        Mockito.when(balanceRepositoryMock.findById(balanceId)).thenReturn(Optional.ofNullable(zeroBalance));

        balanceService.topUpBalance(balanceId, amount);
        Mockito.verify(balanceRepositoryMock).save(balanceCaptor.capture());
        capturedBalance = balanceCaptor.getValue();
        Mockito.verify(balanceRepositoryMock, Mockito.times(1)).save(Mockito.any());

        assertEquals(0, amount.compareTo(capturedBalance.getActualBalance()));
        assertEquals(0, BigDecimal.ZERO.compareTo(capturedBalance.getAuthorizedBalance()));
    }

    @Test
    @DisplayName("Test write-off balance")
    void testWriteOffFunds() {
        Long balanceId = 1L;
        Balance capturedBalance;
        BigDecimal amount = new BigDecimal("10.11");
        Mockito.when(balanceRepositoryMock.findById(balanceId)).thenReturn(Optional.ofNullable(positiveBalance));

        balanceService.writeOffFunds(balanceId, amount);
        Mockito.verify(balanceRepositoryMock).save(balanceCaptor.capture());
        capturedBalance = balanceCaptor.getValue();
        Mockito.verify(balanceRepositoryMock, Mockito.times(1)).save(Mockito.any());

        assertEquals(0, positiveBalanceAmount.subtract(amount).compareTo(capturedBalance.getActualBalance()));
        assertEquals(0, BigDecimal.ZERO.compareTo(capturedBalance.getAuthorizedBalance()));
    }

    @Test
    @DisplayName("Test hold funds on balance")
    void testHoldFunds() {
        Long balanceId = 1L;
        Balance capturedBalance;
        BigDecimal amount = new BigDecimal("10.11");
        Mockito.when(balanceRepositoryMock.findById(balanceId)).thenReturn(Optional.ofNullable(positiveBalance));

        balanceService.holdFunds(balanceId, amount);
        Mockito.verify(balanceRepositoryMock).save(balanceCaptor.capture());
        capturedBalance = balanceCaptor.getValue();
        Mockito.verify(balanceRepositoryMock, Mockito.times(1)).save(Mockito.any());

        assertEquals(0, positiveBalanceAmount.subtract(amount).compareTo(capturedBalance.getActualBalance()));
        assertEquals(0, amount.compareTo(capturedBalance.getAuthorizedBalance()));
    }

    @Test
    @DisplayName("Test releasing held funds")
    void testReleaseFunds() {
        Long balanceId = 1L;
        Balance capturedBalance;
        Mockito.when(balanceRepositoryMock.findById(balanceId)).thenReturn(Optional.ofNullable(heldFundsBalance));

        balanceService.releaseFunds(balanceId, heldAmount);
        Mockito.verify(balanceRepositoryMock).save(balanceCaptor.capture());
        capturedBalance = balanceCaptor.getValue();
        Mockito.verify(balanceRepositoryMock, Mockito.times(1)).save(Mockito.any());

        assertEquals(0, positiveBalanceAmount.compareTo(capturedBalance.getActualBalance()));
        assertEquals(0, BigDecimal.ZERO.compareTo(capturedBalance.getAuthorizedBalance()));
    }

    @Test
    @DisplayName("Test writing off held funds")
    void testWriteOffHeldFunds() {
        Long balanceId = 1L;
        Balance capturedBalance;
        Mockito.when(balanceRepositoryMock.findById(balanceId)).thenReturn(Optional.ofNullable(heldFundsBalance));

        balanceService.writeOffHeldFunds(balanceId, heldAmount);
        Mockito.verify(balanceRepositoryMock).save(balanceCaptor.capture());
        capturedBalance = balanceCaptor.getValue();
        Mockito.verify(balanceRepositoryMock, Mockito.times(1)).save(Mockito.any());

        assertEquals(0, positiveBalanceAmount.subtract(heldAmount).compareTo(capturedBalance.getActualBalance()));
        assertEquals(0, BigDecimal.ZERO.compareTo(capturedBalance.getAuthorizedBalance()));
    }

    @Test
    @DisplayName("Test sufficient for funds")
    void testHasSufficientFunds() {
        Long balanceId = 1L;
        BigDecimal someAmount = new BigDecimal("1.00");
        boolean result;
        Mockito.when(balanceRepositoryMock.findById(balanceId)).thenReturn(Optional.ofNullable(positiveBalance));

        result = balanceService.hasSufficientFunds(balanceId, positiveBalanceAmount);
        assertTrue(result);
        result = balanceService.hasSufficientFunds(balanceId, positiveBalanceAmount.add(someAmount));
        assertFalse(result);
        result = balanceService.hasSufficientFunds(balanceId, positiveBalanceAmount.subtract(someAmount));
        assertTrue(result);

    }

    @Test
    @DisplayName("Test releasing balance")
    void testResetBalance() {
        Long balanceId = 1L;
        Balance capturedBalance;
        Mockito.when(balanceRepositoryMock.findById(balanceId)).thenReturn(Optional.ofNullable(heldFundsBalance));

        balanceService.resetBalance(balanceId);
        Mockito.verify(balanceRepositoryMock).save(balanceCaptor.capture());
        capturedBalance = balanceCaptor.getValue();
        Mockito.verify(balanceRepositoryMock, Mockito.times(1)).save(Mockito.any());

        assertEquals(0, positiveBalanceAmount.compareTo(capturedBalance.getActualBalance()));
        assertEquals(0, BigDecimal.ZERO.compareTo(capturedBalance.getAuthorizedBalance()));
    }

}