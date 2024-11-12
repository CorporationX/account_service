package faang.school.accountservice.service;

import faang.school.accountservice.dto.PendingDto;
import faang.school.accountservice.dto.PendingStatus;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.publisher.PaymentStatusChangePublisher;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final BalanceService balanceService;
    private final BalanceAuditService balanceAuditService;
    private final PaymentStatusChangePublisher paymentStatusChangePublisher;

    @Override
    @Retryable(retryFor = OptimisticLockException.class,
            maxAttemptsExpression = "${payment.cancel.max-attempts}",
            backoff = @Backoff(delayExpression = "${payment.cancel.backoff}")
    )
    public PendingDto cancelPayment(PendingDto pendingDto) {
        paymentAuth(balanceService.getBalanceWithAccountByAccountId(pendingDto.getToAccountId()),
                balanceService.getBalanceWithAccountByAccountId(pendingDto.getFromAccountId()),
                pendingDto.getAmount()
        );

        pendingDto.setStatus(PendingStatus.CANCELED);
        paymentStatusChangePublisher.publish(pendingDto);

        return pendingDto;
    }

    @Override
    @Retryable(retryFor = OptimisticLockException.class,
            maxAttemptsExpression = "${payment.clearing.max-attempts}",
            backoff = @Backoff(delayExpression = "${payment.clearing.backoff}")
    )
    public PendingDto clearingPayment(PendingDto pendingDto) {
        paymentFact(balanceService.getBalanceWithAccountByAccountId(pendingDto.getFromAccountId()),
                balanceService.getBalanceWithAccountByAccountId(pendingDto.getToAccountId()),
                pendingDto.getAmount()
        );

        pendingDto.setStatus(PendingStatus.SUCCESS);
        paymentStatusChangePublisher.publish(pendingDto);

        return pendingDto;
    }

    private void paymentAuth(Balance from, Balance to, BigDecimal amount) {
        from.setCurAuthBalance(from.getCurAuthBalance().subtract(amount));
        to.setCurAuthBalance(to.getCurAuthBalance().add(amount));

        balanceAuditService.recordBalanceChange(from.getCurAuthBalance(), from.getCurAuthBalance(), from);
        balanceAuditService.recordBalanceChange(to.getCurAuthBalance(), to.getCurAuthBalance(), to);
    }

    private void paymentFact(Balance from, Balance to, BigDecimal amount) {
        from.setCurFactBalance(from.getCurFactBalance().subtract(amount));
        to.setCurFactBalance(to.getCurFactBalance().add(amount));

        balanceAuditService.recordBalanceChange(from.getCurFactBalance(), from.getCurFactBalance(), from);
        balanceAuditService.recordBalanceChange(to.getCurFactBalance(), to.getCurFactBalance(), to);
    }
}
