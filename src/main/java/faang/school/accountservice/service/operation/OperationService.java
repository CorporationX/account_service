package faang.school.accountservice.service.operation;

import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.entity.operation.Operation;
import faang.school.accountservice.entity.operation.OperationStatus;
import faang.school.accountservice.entity.operation.OperationType;
import faang.school.accountservice.event.payment.FailedPaymentAuthorizationEventDto;
import faang.school.accountservice.event.payment.PaymentAuthorizationEventDto;
import faang.school.accountservice.event.payment.SuccessPaymentAuthorizationEventDto;
import faang.school.accountservice.publisher.payment.FailedPaymentAuthorizationPublisher;
import faang.school.accountservice.publisher.payment.SuccessPaymentAuthorizationPublisher;
import faang.school.accountservice.repository.operation.OperationRepository;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OperationService {
    private final OperationRepository operationRepository;
    private final BalanceService balanceService;
    private final SuccessPaymentAuthorizationPublisher successPaymentAuthorizationPublisher;
    private final FailedPaymentAuthorizationPublisher failedPaymentAuthorizationPublisher;

    @Transactional
    public void createPaymentOperation(PaymentAuthorizationEventDto paymentAuthorizationEventDto) {
        try {
            balanceService.authorizeBalance(paymentAuthorizationEventDto.getBalanceFromId(), paymentAuthorizationEventDto.getAmount());
            Balance balanceFrom = balanceService.getBalanceById(paymentAuthorizationEventDto.getBalanceFromId());
            Balance balanceTo = balanceService.getBalanceById(paymentAuthorizationEventDto.getBalanceToId());

            Operation operation = getOperation(paymentAuthorizationEventDto, balanceFrom, balanceTo);

            Operation savedOperation = operationRepository.save(operation);

            log.info("Operation {} has been saved", savedOperation);

            SuccessPaymentAuthorizationEventDto successEvent = SuccessPaymentAuthorizationEventDto.builder()
                    .operationToken(paymentAuthorizationEventDto.getOperationToken())
                    .build();
            successPaymentAuthorizationPublisher.sendMessage(successEvent);
        } catch (Exception ex) {
            FailedPaymentAuthorizationEventDto failedEvent = FailedPaymentAuthorizationEventDto.builder()
                    .operationToken(paymentAuthorizationEventDto.getOperationToken())
                    .build();
            failedPaymentAuthorizationPublisher.sendMessage(failedEvent);
        }
    }

    private Operation getOperation(PaymentAuthorizationEventDto paymentAuthorizationEventDto, Balance balanceFrom, Balance balanceTo) {
        Operation operation = new Operation();
        operation.setOperationToken(paymentAuthorizationEventDto.getOperationToken());
        operation.setType(OperationType.PAYMENT);
        operation.setActive(true);
        operation.setBalanceFrom(balanceFrom);
        operation.setBalanceTo(balanceTo);
        operation.setUserId(paymentAuthorizationEventDto.getUserId());
        // TODO: мб при выполнении или отмене операции делать новую запись, а не обновлять существующую
        operation.setStatus(OperationStatus.IN_PROGRESS);
        return operation;
    }
}
