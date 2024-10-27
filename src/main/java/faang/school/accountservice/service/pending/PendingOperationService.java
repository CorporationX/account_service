package faang.school.accountservice.service.pending;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.dto.listener.pending.OperationMessage;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.service.account.AccountService;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PendingOperationService {
    private final AccountService accountService;
    private final BalanceService balanceService;

    public void authorization(OperationMessage operation) {
        Balance balance = accountService.getAccountById(operation.getAccountId()).getBalance();
        balanceService.authorizePayment(balance.getId(), operation.getOperationId(),
                new Money(operation.getAmount(), operation.getCurrency()));
    }

    public void cancellation(OperationMessage operation) {
        balanceService.rejectPayment(operation.getOperationId());
    }

    public void clearing(OperationMessage operation) {
        balanceService.acceptPayment(operation.getOperationId(),
                new Money(operation.getAmount(), operation.getCurrency()));
    }
}
