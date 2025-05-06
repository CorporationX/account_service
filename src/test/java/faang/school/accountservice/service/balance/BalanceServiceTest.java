package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceViewDto;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.service.account.AccountHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private AccountHelper accountHelper;

    @Mock
    private BalanceHelper balanceHelper;

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private BalanceMapper balanceMapper;

    @InjectMocks
    private BalanceService balanceService;

    private final Long accountId = 1L;
    private final BigDecimal amount = new BigDecimal("1000.00");
    private final BalanceViewDto balanceViewDto = new BalanceViewDto();

    @Test
    @DisplayName("Создание баланса, когда счет существует")
    public void givenExistingAccount_whenCreateBalanceForAccountBalance_thenSaveBalance() {
        Account account = new Account();
        when(accountHelper.getAccountById(accountId)).thenReturn(account);

        balanceService.createBalanceForAccount(accountId);

        verify(accountHelper).getAccountById(accountId);
        verify(balanceRepository).save(argThat(balance ->
                balance.getAccount().equals(account)));
    }

    @Test
    @DisplayName("При валидной сумме выполняется авторизация")
    public void givenValidAmount_whenAuthorize_thenAuthorizeAmountBalance() {
        when(balanceHelper.executeBalanceOperation(eq(accountId), any())).thenReturn(balanceViewDto);

        BalanceViewDto result = balanceService.authorizeAmount(accountId, amount);

        assertEquals(balanceViewDto, result);
        verify(balanceHelper).executeBalanceOperation(eq(accountId), any());
    }

    @Test
    @DisplayName("При достаточном авторизованном балансе выполняется клиринг")
    public void givenSufficientAuthBalance_whenClear_thenClearAuthorizedAmountAmount() {
        when(balanceHelper.executeBalanceOperation(eq(accountId), any())).thenReturn(balanceViewDto);

        BalanceViewDto result = balanceService.clearAuthorizedAmount(accountId, amount);

        assertEquals(balanceViewDto, result);
        verify(balanceHelper).executeBalanceOperation(eq(accountId), any());
    }

    @Test
    @DisplayName("Возвращает баланс")
    public void givenExistingBalance_whenGetBalance_thenReturnActualBalance() {
        Balance balance = new Balance();

        when(balanceHelper.getBalance(accountId)).thenReturn(balance);
        when(balanceMapper.toViewDto(balance)).thenReturn(balanceViewDto);

        BalanceViewDto result = balanceService.getBalance(accountId);

        assertNotNull(result);
        verify(balanceHelper).getBalance(accountId);
    }

    @Test
    @DisplayName("Увеличивает баланс")
    public void givenValidAmount_whenDeposit_Amount_thenIncreaseBalance() {
        when(balanceHelper.executeBalanceOperation(eq(accountId), any())).thenReturn(balanceViewDto);

        BalanceViewDto result = balanceService.depositAmount(accountId, amount);

        assertEquals(balanceViewDto, result);
        verify(balanceHelper).executeBalanceOperation(eq(accountId), any());
    }

    @Test
    @DisplayName("Отменяет авторизацию")
    public void givenSufficientAuthBalance_whenCancelAuthorization_thenCancelAuth() {
        when(balanceHelper.executeBalanceOperation(eq(accountId), any())).thenReturn(balanceViewDto);

        BalanceViewDto result = balanceService.cancelAuthorization(accountId, amount);

        assertEquals(balanceViewDto, result);
        verify(balanceHelper).executeBalanceOperation(eq(accountId), any());
    }
}