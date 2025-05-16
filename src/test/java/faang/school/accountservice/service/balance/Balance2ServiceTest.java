package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceViewDto;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.mapper.BalanceMapper2;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance2;
import faang.school.accountservice.repository.BalanceRepository2;
import faang.school.accountservice.service.account.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Balance2ServiceTest {

    @Mock
    private AccountService accountService;

    @Mock
    private BalanceHelper balanceHelper;

    @Mock
    private BalanceRepository2 balanceRepository2;

    @Mock
    private BalanceMapper2 balanceMapper;

    @InjectMocks
    private BalanceService2 balanceService2;

    private final Long accountId = 1L;
    private final BigDecimal amount = new BigDecimal("1000.00");
    private final BalanceViewDto balanceViewDto = new BalanceViewDto();
    private final Balance2 balance2 = new Balance2();

    @Test
    @DisplayName("Создание баланса, когда счет существует")
    public void givenExistingAccount_whenCreateBalanceForAccountBalance_thenSaveBalance() {
        Account account = new Account();
        when(accountService.getAccountById(accountId)).thenReturn(account);

        balanceService2.createBalanceForAccount(accountId);

        verify(accountService).getAccountById(accountId);
        verify(balanceRepository2).save(argThat(balance ->
                balance.getAccount().equals(account)));
    }

    @Test
    @DisplayName("При валидной сумме выполняется авторизация")
    public void givenValidAmount_whenAuthorize_thenAuthorizeAmountBalance() {
        when(balanceHelper.executeBalanceOperation(eq(accountId), any())).thenReturn(balanceViewDto);

        BalanceViewDto result = balanceService2.authorizeAmount(accountId, amount);

        assertEquals(balanceViewDto, result);
        verify(balanceHelper).executeBalanceOperation(eq(accountId), any());
    }

    @Test
    @DisplayName("При достаточном авторизованном балансе выполняется клиринг")
    public void givenSufficientAuthBalance_whenClear_thenClearAuthorizedAmountAmount() {
        when(balanceHelper.executeBalanceOperation(eq(accountId), any())).thenReturn(balanceViewDto);

        BalanceViewDto result = balanceService2.clearAuthorizedAmount(accountId, amount);

        assertEquals(balanceViewDto, result);
        verify(balanceHelper).executeBalanceOperation(eq(accountId), any());
    }

    @Test
    @DisplayName("Возвращает баланс")
    public void givenExistingBalance_whenGetBalance_thenReturnActualBalance() {
        Balance2 balance2 = new Balance2();

        when(balanceRepository2.findByAccountId(accountId)).thenReturn(Optional.of(balance2));
        when(balanceMapper.toViewDto(balance2)).thenReturn(balanceViewDto);

        BalanceViewDto result = balanceService2.getBalance(accountId);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Увеличивает баланс")
    public void givenValidAmount_whenDeposit_Amount_thenIncreaseBalance() {
        when(balanceHelper.executeBalanceOperation(eq(accountId), any())).thenReturn(balanceViewDto);

        BalanceViewDto result = balanceService2.depositAmount(accountId, amount);

        assertEquals(balanceViewDto, result);
        verify(balanceHelper).executeBalanceOperation(eq(accountId), any());
    }

    @Test
    @DisplayName("Отменяет авторизацию")
    public void givenSufficientAuthBalance_whenCancelAuthorization_thenCancelAuth() {
        when(balanceHelper.executeBalanceOperation(eq(accountId), any())).thenReturn(balanceViewDto);

        BalanceViewDto result = balanceService2.cancelAuthorization(accountId, amount);

        assertEquals(balanceViewDto, result);
        verify(balanceHelper).executeBalanceOperation(eq(accountId), any());
    }

    @Test
    @DisplayName("Получение баланса, когда счет существует")
    public void givenExistingAccountId_whenGetBalanceEntity_thenReturnBalance() {
        when(balanceRepository2.findByAccountId(accountId))
                .thenReturn(Optional.of(balance2));

        Balance2 result = balanceService2.getBalanceEntity(accountId);

        assertNotNull(result);
        assertEquals(balance2, result);
        verify(balanceRepository2).findByAccountId(accountId);
    }

    @Test
    @DisplayName("Получение баланса, когда счет не существует - должен выбросить исключение")
    public void givenNonExistingAccountId_whenGetBalanceEntity_thenThrowException() {
        when(balanceRepository2.findByAccountId(accountId))
                .thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () ->
                balanceService2.getBalanceEntity(accountId));
        verify(balanceRepository2).findByAccountId(accountId);
    }
}