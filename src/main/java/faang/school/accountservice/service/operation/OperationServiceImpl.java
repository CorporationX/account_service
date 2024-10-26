package faang.school.accountservice.service.operation;

import faang.school.accountservice.entity.PaymentAccount;
import faang.school.accountservice.entity.PendingOperation;
import faang.school.accountservice.listener.event.PaymentRequestEvent;
import faang.school.accountservice.repository.PaymentAccountRepository;
import faang.school.accountservice.repository.operation.OperationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperationServiceImpl {

    OperationRepository operationRepository;
    PaymentAccountRepository paymentAccountRepository;

    public void save(PaymentRequestEvent event) {

        PaymentAccount paymentAccount = paymentAccountRepository.findById(event.getUserId()).orElseThrow(
                () -> {
                    log.error("Account not found for user with id {}", event.getUserId());
                    return new EntityNotFoundException(String.format("Account not found for user with id %s", event.getUserId()));
                });
        PendingOperation operation = new PendingOperation();
        operation.setAccountId(paymentAccount.getId());
        operation.setAmount(event.getAmount());
        operationRepository.save(operation);
    }
}
