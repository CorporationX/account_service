package faang.school.accountservice.service.operation;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.PaymentAccount;
import faang.school.accountservice.entity.PendingOperation;
import faang.school.accountservice.enums.OperationState;
import faang.school.accountservice.listener.event.PaymentRequestEvent;
import faang.school.accountservice.repository.PaymentAccountRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import faang.school.accountservice.repository.operation.OperationRepository;
import faang.school.accountservice.service.balance.BalanceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;
    private final PaymentAccountRepository paymentAccountRepository;
    private final BalanceRepository balanceRepository;
    private final BalanceService balanceService;

    public void handlePaymentRequest(PaymentRequestEvent paymentRequest) {

        PaymentAccount paymentAccount = paymentAccountRepository.findByUserId(paymentRequest.getUserId()).orElseThrow(
                () -> {
                    log.error("Account not found for user with id {}", paymentRequest.getUserId());
                    return new EntityNotFoundException(
                            String.format("Account not found for user with id %s", paymentRequest.getUserId()));
                });
        PendingOperation operation = createOperation(paymentAccount, paymentRequest);
        Balance balance = paymentAccount.getBalance();
        if (hasSufficientBalance(balance, paymentRequest.getAmount())) {
            operation.setState(OperationState.PENDING);
            operation = operationRepository.save(operation);
            reserveFunds(paymentAccount.getBalance(), paymentRequest.getAmount(), operation);
            log.info("Operation confirmed for account ID: {}", balance.getId());
        } else {
            operation.setState(OperationState.CANCELED);
            operationRepository.save(operation);
            log.info("Operation canceled due to insufficient funds for account ID: {}", balance.getId());
        }

    }

    private PendingOperation createOperation(PaymentAccount paymentAccount, PaymentRequestEvent paymentRequest) {
        PendingOperation operation = new PendingOperation();
        operation.setAccountId(paymentAccount.getId());
        operation.setAmount(paymentRequest.getAmount());
        return operation;
    }

    private boolean hasSufficientBalance(Balance balance, BigDecimal amount) {
        return balance.getAuthorizedBalance().compareTo(amount) > 0;
    }

    private void reserveFunds(Balance balance, BigDecimal amount, PendingOperation operation) {
        BalanceDto balanceDto = new BalanceDto(
                balance.getAuthorizedBalance().subtract(amount),
                balance.getActualBalance()
        );
        balanceService.updateBalance(balance.getId(), balanceDto, operation);
        log.info("Funds successfully reserved for account ID: {}", balance.getId());
    }

}
