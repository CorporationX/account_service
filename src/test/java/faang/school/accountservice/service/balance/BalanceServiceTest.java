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
        when(balanceRepository.findByIdForUpdate(balanceId)).thenReturn(Optional.of(balance));

        BigDecimal amount = new BigDecimal("300");
        BigDecimal available = balance.getBalance().subtract(balance.getAuthorizedBalance()); // 800

        // Проверяем, что валидатор вызывается
        doNothing().when(balanceValidator).validateAmountDoesNotExceedLimit(balanceId, amount, available);

        Balance result = balanceService.authorizeBalance(balanceId, amount);

        // authorizedBalance увеличен
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
        when(balanceRepository.findByIdForUpdate(balanceId)).thenReturn(Optional.of(balance));

        BigDecimal amount = new BigDecimal("100");
        doNothing().when(balanceValidator).validateAmountDoesNotExceedLimit(balanceId, amount, balance.getAuthorizedBalance());

        Balance result = balanceService.clearAuthorizationBalance(balanceId, amount);

        assertThat(result.getAuthorizedBalance()).isEqualByComparingTo(new BigDecimal("100")); // 200 - 100
        assertThat(result.getBalance()).isEqualByComparingTo(new BigDecimal("900")); // 1000 - 100

        verify(balanceValidator).validateAmountDoesNotExceedLimit(balanceId, amount, balance.getAuthorizedBalance());
    }

    @Test
    void cancelAuthorizationBalance_success() {
        when(balanceRepository.findByIdForUpdate(balanceId)).thenReturn(Optional.of(balance));

        BigDecimal amount = new BigDecimal("50");
        doNothing().when(balanceValidator).validateAmountDoesNotExceedLimit(balanceId, amount, balance.getAuthorizedBalance());

        Balance result = balanceService.cancelAuthorizationBalance(balanceId, amount);

        assertThat(result.getAuthorizedBalance()).isEqualByComparingTo(new BigDecimal("150")); // 200 - 50

        verify(balanceValidator).validateAmountDoesNotExceedLimit(balanceId, amount, balance.getAuthorizedBalance());
    }
}
