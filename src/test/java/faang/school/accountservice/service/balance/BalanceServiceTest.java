package faang.school.accountservice.service.balance;

import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.exception.balance.BalanceNotFoundException;
import faang.school.accountservice.repository.balance.BalanceRepository;
import faang.school.accountservice.validation.balance.BalanceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private BalanceValidator balanceValidator;

    @InjectMocks
    private BalanceService balanceService;

    private UUID balanceId;
    private Balance balance;

    @BeforeEach
    void setUp() {
        balanceId = UUID.randomUUID();
        balance = new Balance();
        balance.setId(balanceId);
        balance.setBalance(new BigDecimal("1000"));
        balance.setAuthorizedBalance(new BigDecimal("200"));
    }

    @Test
    void getBalanceById_whenExists_returnsBalance() {
        when(balanceRepository.findById(balanceId)).thenReturn(Optional.of(balance));

        Balance result = balanceService.getBalanceById(balanceId);

        assertThat(result).isEqualTo(balance);
    }

    @Test
    void getBalanceById_whenNotFound_throwsException() {
        when(balanceRepository.findById(balanceId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> balanceService.getBalanceById(balanceId))
                .isInstanceOf(BalanceNotFoundException.class);
    }

    @Test
    void authorizeBalance_success() {
        BigDecimal amount = new BigDecimal("300");
        BigDecimal available = balance.getBalance().subtract(balance.getAuthorizedBalance());

        when(balanceRepository.findByIdForUpdate(balanceId)).thenReturn(Optional.of(balance));
        doNothing().when(balanceValidator).validateAmountDoesNotExceedLimit(balanceId, amount, available);

        Balance result = balanceService.authorizeBalance(balanceId, amount);

        assertThat(result.getAuthorizedBalance()).isEqualByComparingTo(new BigDecimal("500"));
        verify(balanceValidator).validateAmountDoesNotExceedLimit(balanceId, amount, available);
    }

    @Test
    void authorizeBalance_whenBalanceNotFound_throwsException() {
        when(balanceRepository.findByIdForUpdate(balanceId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> balanceService.authorizeBalance(balanceId, BigDecimal.TEN))
                .isInstanceOf(BalanceNotFoundException.class);
    }

    @Test
    void clearAuthorizationBalance_success() {
        BigDecimal amount = new BigDecimal("100");
        BigDecimal authBalance = balance.getAuthorizedBalance();

        when(balanceRepository.findByIdForUpdate(balanceId)).thenReturn(Optional.of(balance));
        doNothing().when(balanceValidator).validateAmountDoesNotExceedLimit(balanceId, amount, authBalance);

        Balance result = balanceService.clearAuthorizationBalance(balanceId, amount);

        assertThat(result.getAuthorizedBalance()).isEqualByComparingTo(new BigDecimal("100"));
        assertThat(result.getBalance()).isEqualByComparingTo(new BigDecimal("900"));
        verify(balanceValidator).validateAmountDoesNotExceedLimit(balanceId, amount, authBalance);
    }

    @Test
    void cancelAuthorizationBalance_success() {
        BigDecimal amount = new BigDecimal("50");
        BigDecimal authBalance = balance.getAuthorizedBalance();

        when(balanceRepository.findByIdForUpdate(balanceId)).thenReturn(Optional.of(balance));
        doNothing().when(balanceValidator).validateAmountDoesNotExceedLimit(balanceId, amount, authBalance);

        Balance result = balanceService.cancelAuthorizationBalance(balanceId, amount);

        assertThat(result.getAuthorizedBalance()).isEqualByComparingTo(new BigDecimal("150"));
        verify(balanceValidator).validateAmountDoesNotExceedLimit(balanceId, amount, authBalance);
    }
}
