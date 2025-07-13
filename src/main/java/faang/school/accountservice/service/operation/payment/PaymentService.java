package faang.school.accountservice.service.operation.payment;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.AccountOwnerType;
import faang.school.accountservice.entity.currency.Currency;
import faang.school.accountservice.entity.operation.Operation;
import faang.school.accountservice.entity.operation.OperationType;
import faang.school.accountservice.event.payment.PaymentAuthorizationEventDto;
import faang.school.accountservice.exception.operation.payment.AccountCurrencyMismatchException;
import faang.school.accountservice.exception.operation.payment.InvalidAccountOwnerException;
import faang.school.accountservice.exception.operation.payment.UnauthorizedAccountAccessException;
import faang.school.accountservice.publisher.payment.FailedPaymentAuthorizationPublisher;
import faang.school.accountservice.publisher.payment.SuccessPaymentAuthorizationPublisher;
import faang.school.accountservice.service.account.AccountService;
import faang.school.accountservice.service.balance.BalanceService;
import faang.school.accountservice.service.currency.CurrencyService;
import faang.school.accountservice.service.operation.OperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    private final OperationService operationService;
    private final BalanceService balanceService;
    private final AccountService accountService;
    private final CurrencyService currencyService;
    private final PaymentGeneratorLock paymentGeneratorLock;

    @Transactional
    public Operation createPaymentOperation(PaymentAuthorizationEventDto paymentEvent) {
        // 1 - оба аккаунта существуют
        // 2 - валюта существует
        // 3 - нужная валюта у обоих аккаунтов
        // 4 - достаточно средств для авторизации
        // 5 - тот же пользователь, что и владелец ассаунта from
        // 6 - нет уже такого токена операции
        // 7 - лок не занят
        // 8 (в event) - сумма не меньше 1 и ровная
        Account accountFrom = accountService.getAccountById(paymentEvent.getAccountFromId());
        Account accountTo = accountService.getAccountById(paymentEvent.getAccountToId());
        Currency currency = currencyService.getCurrencyById(paymentEvent.getCurrencyId());
        if (!Objects.equals(accountFrom.getCurrency(), currency) ||
                !Objects.equals(accountTo.getCurrency(), currency)) {
            throw new AccountCurrencyMismatchException("");
        }
        if (!Objects.equals(accountFrom.getOwnerType(), AccountOwnerType.USER)) {
            throw new InvalidAccountOwnerException("");
        }
        if (!Objects.equals(accountFrom.getUserId(), paymentEvent.getUserId())) {
            throw new UnauthorizedAccountAccessException("");
        }
        balanceService.authorizeBalance(accountFrom.getBalance().getId(), paymentEvent.getAmount());

        String paymentLock = paymentGeneratorLock.buildPaymentLock(paymentEvent.getUserId());

        return operationService.createOperation(
                paymentEvent.getOperationToken(),
                OperationType.PAYMENT,
                paymentEvent.getUserId(),
                paymentEvent.getAccountFromId(),
                paymentEvent.getAccountToId(),
                paymentLock,
                // TODO: json
                paymentEvent.toString()
        );
    }
}
