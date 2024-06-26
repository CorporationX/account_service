package faang.school.accountservice.controller;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.service.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceControllerTest {

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private BalanceController balanceController;

    private BalanceDto balanceDto;
    private BigDecimal newCurrentBalance;
    private BigDecimal newAuthorizedBalance;
    private Long accountId;

    @BeforeEach
    void setUp() {
        accountId = 1L;
        balanceDto = BalanceDto.builder()
                .id(1L)
                .currentBalance(BigDecimal.ZERO)
                .authorizedBalance(BigDecimal.ZERO)
                .build();
        newCurrentBalance = BigDecimal.TEN;
        newAuthorizedBalance = BigDecimal.valueOf(100);
    }

    @Test
    void createBalanceShouldReturnCreatedBalance() {
        when(balanceService.createBalance(accountId)).thenReturn(balanceDto);
        BalanceDto result = balanceController.createBalance(accountId);
        assertThat(result).isEqualTo(balanceDto);
    }

    @Test
    void getBalanceShouldReturnBalance() {
        when(balanceService.getBalance(accountId)).thenReturn(balanceDto);
        BalanceDto result = balanceController.getBalance(accountId);
        assertThat(result).isEqualTo(balanceDto);
    }

    @Test
    void updateBalanceShouldReturnUpdatedBalance() {
        when(balanceService.updateBalance(accountId, newCurrentBalance, newAuthorizedBalance))
                .thenReturn(balanceDto);
        BalanceDto result = balanceController.updateBalance(accountId, newCurrentBalance, newAuthorizedBalance);
        assertThat(result).isEqualTo(balanceDto);
    }
}