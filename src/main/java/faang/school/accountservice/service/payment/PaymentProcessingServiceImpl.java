package faang.school.accountservice.service.payment;

import faang.school.accountservice.client.PaymentServiceClient;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.dto.event.PaymentEvent;
import faang.school.accountservice.mapper.PaymentKeyGenerator;
import faang.school.accountservice.service.BalanceService;
import faang.school.accountservice.service.account.AccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentProcessingServiceImpl implements PaymentProcessingService {
    private final AccountService accountService;
    private final BalanceService balanceService;
    private final PaymentServiceClient paymentServiceClient;
    private  final PaymentHistoryService paymentHistoryService;
    private final PaymentKeyGenerator paymentKeyGenerator;


    public void reserveMoney(PaymentEvent paymentEvent) {
        if (checkPaymentAlreadyExist(paymentEvent)) {
            return;
        }
        if (!checkAccountsExist(paymentEvent)) {
            paymentServiceClient.cancelPayment(paymentEvent.getPaymentId());
            return;
        }
        AccountDto accountDto = accountService.getByNumber(paymentEvent.getRequesterNumber());
        if (accountDto.getBalance().compareTo(paymentEvent.getAmount()) <= 0) {
            paymentServiceClient.cancelPayment(paymentEvent.getPaymentId());
        } else {
            BalanceDto balance = balanceService.getBalance(accountDto.getId());
            BigDecimal newCurrentBalance = balance.getCurrentBalance().subtract(paymentEvent.getAmount());
            BigDecimal newAuthorizationBalance = balance.getAuthorizedBalance().add(paymentEvent.getAmount());
            balanceService.updateBalance(accountDto.getId(), newCurrentBalance, newAuthorizationBalance);
            paymentHistoryService.save(paymentEvent);
        }
    }

    public void cancelPayment(PaymentEvent paymentEvent) {
        if (checkPaymentAlreadyExist(paymentEvent)) {
            return;
        }
        AccountDto accountDto = accountService.getByNumber(paymentEvent.getRequesterNumber());
        BalanceDto balance = balanceService.getBalance(accountDto.getId());
        if (balance.getAuthorizedBalance().intValue() >= paymentEvent.getAmount().intValue()) {
            BigDecimal newCurrentBalance = balance.getCurrentBalance().add(paymentEvent.getAmount());
            BigDecimal newAuthorizationBalance = balance.getAuthorizedBalance().subtract(paymentEvent.getAmount());
            balanceService.updateBalance(accountDto.getId(), newCurrentBalance, newAuthorizationBalance);
            paymentHistoryService.save(paymentEvent);
        }
    }

    public void clearing(PaymentEvent paymentEvent) {
        if (checkPaymentAlreadyExist(paymentEvent)) {
            return;
        }
        AccountDto requester = accountService.getByNumber(paymentEvent.getRequesterNumber());
        AccountDto receiver = accountService.getByNumber(paymentEvent.getReceiverNumber());
        BalanceDto requesterBalance = balanceService.getBalance(requester.getId());
        BalanceDto receiverBalance = balanceService.getBalance(requester.getId());
        balanceService.updateBalance(requester.getId(), requesterBalance.getCurrentBalance(),
                requesterBalance.getAuthorizedBalance().subtract(paymentEvent.getAmount()));
        balanceService.updateBalance(receiver.getId(), receiverBalance.getCurrentBalance().add(paymentEvent.getAmount()),
                receiverBalance.getAuthorizedBalance());
        passPayment(paymentEvent);
        paymentHistoryService.save(paymentEvent);
    }

    @Transactional
    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 3000L, multiplier = 1.5))
    public void passPayment(PaymentEvent paymentEvent) {
        paymentServiceClient.passPayment(paymentEvent.getPaymentId());
    }

    private boolean checkAccountsExist(PaymentEvent paymentEvent) {
        return accountService.existsByNumber(paymentEvent.getRequesterNumber())
                || accountService.existsByNumber(paymentEvent.getReceiverNumber());
    }

    private String generateIdempotencyKey(PaymentEvent paymentEvent) {
        return paymentKeyGenerator.generateKey(paymentEvent).toString();
    }

    private boolean checkPaymentAlreadyExist(PaymentEvent paymentEvent) {
        return paymentHistoryService.existsByIdempotencyKey(generateIdempotencyKey(paymentEvent));
    }
}