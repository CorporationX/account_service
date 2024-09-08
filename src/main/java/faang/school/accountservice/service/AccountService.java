package faang.school.accountservice.service;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final BalanceService balanceService;

    @Async("balanceOperationExecutor")
    public CompletableFuture<PaymentStatus> suspendBalance(Balance balance, BigDecimal sum) {
        return CompletableFuture.completedFuture(balanceService.suspendBalance(balance, sum));
    }

    @Async("balanceOperationExecutor")
    public void createBalance() {
        balanceService.createBalance();
    }

    @Async("balanceOperationExecutor")
    public CompletableFuture<PaymentStatus> receivingBalance(Balance balance, BigDecimal sum) {
        return CompletableFuture.completedFuture(balanceService.receivingBalance(balance, sum));
    }

    @Async("balanceOperationExecutor")
    public CompletableFuture<PaymentStatus> spendingAuthorizationBalance(Balance balance, BigDecimal sum) {
        return CompletableFuture.completedFuture(balanceService.spendingAuthorizationBalance(balance, sum));
    }

    @Async("balanceOperationExecutor")
    public CompletableFuture<PaymentStatus> spendingBalance(Balance balance, BigDecimal sum) {
        return CompletableFuture.completedFuture(balanceService.spendingBalance(balance, sum));
    }
}
