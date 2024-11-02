package faang.school.accountservice.service.pending;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.dto.balance.response.CheckingAccountBalance;
import faang.school.accountservice.dto.listener.pending.OperationMessage;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.pending.Category;
import faang.school.accountservice.publisher.pending.PendingOperationStatusPublisher;
import faang.school.accountservice.service.account.AccountService;
import faang.school.accountservice.service.balance.BalanceService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static faang.school.accountservice.enums.Currency.USD;
import static faang.school.accountservice.enums.pending.AccountBalanceStatus.SUFFICIENT_FUNDS;
import static faang.school.accountservice.enums.pending.Category.OTHER;
import static faang.school.accountservice.util.fabrics.AccountFabric.buildAccount;
import static faang.school.accountservice.util.fabrics.BalanceFabric.buildBalance;
import static faang.school.accountservice.util.fabrics.OperationMessageFabric.buildOperationMessage;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PendingOperationServiceTest {
    private static final UUID OPERATION_ID = UUID.randomUUID();
    private static final UUID SOURCE_ACCOUNT_ID = UUID.randomUUID();
    private static final UUID TARGET_ACCOUNT_ID = UUID.randomUUID();
    private static final UUID SOURCE_BALANCE_ID = UUID.randomUUID();
    private static final UUID TARGET_BALANCE_ID = UUID.randomUUID();
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(20);
    private static final Currency CURRENCY = USD;

    @Mock
    private AccountService accountService;

    @Mock
    private BalanceService balanceService;

    @Mock
    private PendingOperationStatusPublisher pendingOperationStatusPublisher;

    @InjectMocks
    private PendingOperationService pendingOperationService;

    @Test
    void testAuthorization_successful() {
        OperationMessage operation = buildOperationMessage(OPERATION_ID, SOURCE_ACCOUNT_ID, TARGET_ACCOUNT_ID,
                AMOUNT, CURRENCY, OTHER);
        Balance sourceBalance = buildBalance(SOURCE_BALANCE_ID);
        Balance targetBalance = buildBalance(TARGET_BALANCE_ID);
        Account sourceAccount = buildAccount(sourceBalance);
        Account targetAccount = buildAccount(targetBalance);
        when(accountService.getAccountById(SOURCE_ACCOUNT_ID)).thenReturn(sourceAccount);
        when(accountService.getAccountById(TARGET_ACCOUNT_ID)).thenReturn(targetAccount);

        pendingOperationService.authorization(operation);

        ArgumentCaptor<OperationMessage> operationCaptor = ArgumentCaptor.forClass(OperationMessage.class);
        ArgumentCaptor<Balance> sourceBalanceCaptor = ArgumentCaptor.forClass(Balance.class);
        ArgumentCaptor<Balance> targetBalanceCaptor = ArgumentCaptor.forClass(Balance.class);
        ArgumentCaptor<Money> moneyCaptor = ArgumentCaptor.forClass(Money.class);
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        ArgumentCaptor<CheckingAccountBalance> checkingAccountBalanceCaptor =
                ArgumentCaptor.forClass(CheckingAccountBalance.class);

        verify(balanceService).authorizePayment(operationCaptor.capture(), sourceBalanceCaptor.capture(),
                targetBalanceCaptor.capture(), moneyCaptor.capture(), categoryCaptor.capture());
        verify(pendingOperationStatusPublisher).publish(checkingAccountBalanceCaptor.capture());

        assertThat(operationCaptor.getValue()).isEqualTo(operation);

        assertThat(sourceBalanceCaptor.getValue()).isEqualTo(sourceBalance);
        assertThat(targetBalanceCaptor.getValue()).isEqualTo(targetBalance);

        Money money = moneyCaptor.getValue();
        assertThat(money.amount()).isEqualTo(AMOUNT);
        assertThat(money.currency()).isEqualTo(CURRENCY);

        assertThat(categoryCaptor.getValue()).isEqualTo(OTHER);

        CheckingAccountBalance checkingAccountBalance = checkingAccountBalanceCaptor.getValue();
        Assertions.assertThat(checkingAccountBalance.getOperationId()).isEqualTo(OPERATION_ID);
        Assertions.assertThat(checkingAccountBalance.getSourceAccountId()).isEqualTo(SOURCE_ACCOUNT_ID);
        Assertions.assertThat(checkingAccountBalance.getStatus()).isEqualTo(SUFFICIENT_FUNDS);
    }

    @Test
    void testClearing_successful() {
        OperationMessage operation = buildOperationMessage(OPERATION_ID, SOURCE_ACCOUNT_ID, TARGET_ACCOUNT_ID,
                AMOUNT, CURRENCY, OTHER);

        pendingOperationService.clearing(operation);

        ArgumentCaptor<UUID> operationIdCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Money> moneyCaptor = ArgumentCaptor.forClass(Money.class);

        verify(balanceService).acceptPayment(operationIdCaptor.capture(), moneyCaptor.capture());

        assertThat(operationIdCaptor.getValue()).isEqualTo(OPERATION_ID);

        Money money = moneyCaptor.getValue();
        assertThat(money.amount()).isEqualTo(AMOUNT);
        assertThat(money.currency()).isEqualTo(CURRENCY);
    }

    @Test
    void testCancellation_successful() {
        OperationMessage operation = buildOperationMessage(OPERATION_ID);
        pendingOperationService.cancellation(operation);
        verify(balanceService).rejectPayment(OPERATION_ID);
    }
}
