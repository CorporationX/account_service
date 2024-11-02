package faang.school.accountservice.service.pending;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.dto.balance.response.CheckingAccountBalance;
import faang.school.accountservice.dto.listener.pending.OperationMessage;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.publisher.pending.PendingOperationStatusPublisher;
import faang.school.accountservice.service.account.AccountService;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static faang.school.accountservice.enums.pending.AccountBalanceStatus.SUFFICIENT_FUNDS;

@Slf4j
@RequiredArgsConstructor
@Service
public class PendingOperationService {
    private final AccountService accountService;
    private final BalanceService balanceService;
    private final PendingOperationStatusPublisher publisher;

    public void authorization(OperationMessage operation) {
        Balance sourceBalance = accountService.getAccountById(operation.getSourceAccountId()).getBalance();
        Balance targetBalance = accountService.getAccountById(operation.getTargetAccountId()).getBalance();
        Money money = new Money(operation.getAmount(), operation.getCurrency());

        balanceService.authorizePayment(operation, sourceBalance, targetBalance, money, operation.getCategory());

        publisher.publish(new CheckingAccountBalance(operation.getOperationId(), operation.getSourceAccountId(),
                SUFFICIENT_FUNDS));
    }

    public void clearing(OperationMessage operation) {
        Money money = new Money(operation.getAmount(), operation.getCurrency());

        balanceService.acceptPayment(operation.getOperationId(), money);
    }

    public void cancellation(OperationMessage operation) {
        balanceService.rejectPayment(operation.getOperationId());
    }
}
