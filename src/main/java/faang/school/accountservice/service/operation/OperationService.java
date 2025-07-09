package faang.school.accountservice.service.operation;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.AccountOwnerType;
import faang.school.accountservice.entity.currency.Currency;
import faang.school.accountservice.entity.operation.Operation;
import faang.school.accountservice.entity.operation.OperationStatus;
import faang.school.accountservice.entity.operation.OperationType;
import faang.school.accountservice.event.payment.FailedPaymentAuthorizationEventDto;
import faang.school.accountservice.event.payment.PaymentAuthorizationEventDto;
import faang.school.accountservice.event.payment.SuccessPaymentAuthorizationEventDto;
import faang.school.accountservice.publisher.payment.FailedPaymentAuthorizationPublisher;
import faang.school.accountservice.publisher.payment.SuccessPaymentAuthorizationPublisher;
import faang.school.accountservice.repository.operation.OperationRepository;
import faang.school.accountservice.service.account.AccountService;
import faang.school.accountservice.service.balance.BalanceService;
import faang.school.accountservice.service.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class OperationService {
    private final OperationRepository operationRepository;
    private final BalanceService balanceService;
    private final AccountService accountService;
    private final CurrencyService currencyService;
    private final SuccessPaymentAuthorizationPublisher successPaymentAuthorizationPublisher;
    private final FailedPaymentAuthorizationPublisher failedPaymentAuthorizationPublisher;

    @Transactional
    public void createPaymentOperation(PaymentAuthorizationEventDto eventDto) {
        try {
            // 1 - оба аккаунта существуют
            // 2 - валюта существует
            // 3 - нужная валюта у обоих аккаунтов
            // 4 - достаточно средств для авторизации
            // 5 - тот же пользователь, что и владелец ассаунта from
            // 6 - нет уже такого токена операции
            // 7 - лок не занят
            // 8 (в event) - сумма не меньше 1 и ровная
            Account accountFrom = accountService.getAccountById(eventDto.getAccountFromId());
            Account accountTo = accountService.getAccountById(eventDto.getAccountToId());
            Currency currency = currencyService.getCurrencyById(eventDto.getCurrencyId());
            if (!Objects.equals(accountFrom.getCurrency(), currency) ||
                    !Objects.equals(accountTo.getCurrency(), currency)) {
                throw new RuntimeException();
            }
            if (!Objects.equals(accountFrom.getOwnerType(), AccountOwnerType.USER)) {
                throw new RuntimeException();
            }
            if (!Objects.equals(accountFrom.getUserId(), eventDto.getUserId())) {
                throw new RuntimeException();
            }
            balanceService.authorizeBalance(accountFrom.getBalance().getId(), eventDto.getAmount());

            Operation operation = getPaymentOperation(eventDto, accountFrom, accountTo);

            Operation savedOperation = operationRepository.save(operation);

            log.info("Operation {} has been saved", savedOperation);

            SuccessPaymentAuthorizationEventDto successEvent = new SuccessPaymentAuthorizationEventDto(eventDto.getOperationToken());
            successPaymentAuthorizationPublisher.sendMessage(successEvent);
        } catch (RuntimeException ex) {
            log.info("fsd");
        } catch (Exception ex) {
            FailedPaymentAuthorizationEventDto failedEvent = new FailedPaymentAuthorizationEventDto(eventDto.getOperationToken());
            failedPaymentAuthorizationPublisher.sendMessage(failedEvent);
        }
    }

    private Operation getPaymentOperation(PaymentAuthorizationEventDto paymentEvent,
                                          Account accountFrom, Account accountTo) {
        Operation operation = new Operation();
        operation.setOperationToken(paymentEvent.getOperationToken());
        operation.setType(OperationType.PAYMENT);
        operation.setActive(true);
        operation.setAccountFrom(accountFrom);
        operation.setAccountTo(accountTo);
        operation.setUserId(paymentEvent.getUserId());
        // TODO: как будет вести себя unique index с null
        operation.setLockedBy(String.valueOf(paymentEvent.getUserId()));
        // TODO: мб при выполнении или отмене операции делать новую запись, а не обновлять существующую
        operation.setStatus(OperationStatus.IN_PROGRESS);
        return operation;
    }
}
