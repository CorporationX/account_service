package faang.school.accountservice.service.pending;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.dto.listener.pending.OperationMessage;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.service.account.AccountService;
import faang.school.accountservice.service.balance.BalanceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static faang.school.accountservice.enums.Currency.USD;
import static faang.school.accountservice.util.fabrics.AccountFabric.buildAccount;
import static faang.school.accountservice.util.fabrics.BalanceFabric.buildBalance;
import static faang.school.accountservice.util.fabrics.OperationMessageFabric.buildOperationMessage;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PendingOperationServiceTest {
    private static final UUID OPERATION_ID = UUID.randomUUID();
    private static final UUID ACCOUNT_ID = UUID.randomUUID();
    private static final UUID BALANCE_ID = UUID.randomUUID();
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(20);
    private static final Currency CURRENCY = USD;

    @Mock
    private AccountService accountService;

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private PendingOperationService pendingOperationService;

    @Test
    void testAuthorization_successful() {
        OperationMessage operation = buildOperationMessage(OPERATION_ID, ACCOUNT_ID, AMOUNT, CURRENCY);
        Balance balance = buildBalance(BALANCE_ID);
        Account account = buildAccount(balance);
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(account);

        pendingOperationService.authorization(operation);

        ArgumentCaptor<Money> moneyCaptor = ArgumentCaptor.forClass(Money.class);
        ArgumentCaptor<UUID> balanceIdCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<UUID> operationIdCaptor = ArgumentCaptor.forClass(UUID.class);

        verify(balanceService).authorizePayment(balanceIdCaptor.capture(), operationIdCaptor.capture(), moneyCaptor.capture());

        Money money = moneyCaptor.getValue();
        assertThat(money.amount()).isEqualTo(AMOUNT);
        assertThat(money.currency()).isEqualTo(CURRENCY);

        assertThat(balanceIdCaptor.getValue()).isEqualTo(BALANCE_ID);
        assertThat(operationIdCaptor.getValue()).isEqualTo(OPERATION_ID);
    }

    @Test
    void testCancellation_successful() {
        OperationMessage operation = buildOperationMessage(OPERATION_ID);
        pendingOperationService.cancellation(operation);
        verify(balanceService).rejectPayment(OPERATION_ID);
    }

    @Test
    void testClearing_successful() {
        OperationMessage operation = buildOperationMessage(OPERATION_ID, ACCOUNT_ID, AMOUNT, CURRENCY);

        pendingOperationService.clearing(operation);

        ArgumentCaptor<Money> moneyCaptor = ArgumentCaptor.forClass(Money.class);
        ArgumentCaptor<UUID> operationIdCaptor = ArgumentCaptor.forClass(UUID.class);

        verify(balanceService).acceptPayment(operationIdCaptor.capture(), moneyCaptor.capture());

        Money money = moneyCaptor.getValue();
        assertThat(money.amount()).isEqualTo(AMOUNT);
        assertThat(money.currency()).isEqualTo(CURRENCY);
        assertThat(operationIdCaptor.getValue()).isEqualTo(OPERATION_ID);
    }
}