package faang.school.accountservice.service;

import faang.school.accountservice.dto.PendingDto;
import faang.school.accountservice.dto.PendingStatus;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.publisher.PaymentStatusChangePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final BalanceService balanceService;
    private final PaymentStatusChangePublisher paymentStatusChangePublisher;

    @Override
    @Transactional
    @Async("mainExecutorService")
    public void paymentAuthorization(@Validated PendingDto pendingDto) {
        Balance fromBalance = balanceService.getBalanceByAccountId(pendingDto.getFromAccountId());
        Balance toBalance = balanceService.getBalanceByAccountId(pendingDto.getToAccountId());
        BigDecimal amount = pendingDto.getAmount();

        if (fromBalance.getCurAuthBalance().compareTo(amount) < 0) {
            pendingDto.setStatus(PendingStatus.CANCELED);
            paymentStatusChangePublisher.publish(pendingDto);
            log.warn("Authorization failed. Insufficient funds: Available balance: {}", fromBalance.getCurAuthBalance());
            return;
        }

        fromBalance.setCurAuthBalance(fromBalance.getCurAuthBalance().subtract(amount));
        toBalance.setCurAuthBalance(toBalance.getCurAuthBalance().add(amount));

        log.info("Authorization successful. New fromBalance: {}, New toBalance: {}",
                fromBalance.getCurAuthBalance(), toBalance.getCurAuthBalance());
    }

    @Override
    @Transactional
    @Async("mainExecutorService")
    public void clearPayment(@Validated PendingDto pendingDto) {
        Balance fromBalance = balanceService.getBalanceByAccountId(pendingDto.getFromAccountId());
        Balance toBalance = balanceService.getBalanceByAccountId(pendingDto.getToAccountId());
        BigDecimal amount = pendingDto.getAmount();

        fromBalance.setCurFactBalance(fromBalance.getCurFactBalance().subtract(amount));
        toBalance.setCurFactBalance(toBalance.getCurFactBalance().add(amount));

        pendingDto.setStatus(PendingStatus.SUCCESS);
        paymentStatusChangePublisher.publish(pendingDto);

        log.info("Clearing successful. New fromBalance: {}, New toBalance: {}",
                fromBalance.getCurFactBalance(), toBalance.getCurFactBalance());
    }
}
