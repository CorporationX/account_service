package faang.school.accountservice.service.request_task.handler.impl.create_account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.enums.request_task.RequestTaskStatus;
import faang.school.accountservice.enums.request_task.RequestTaskType;
import faang.school.accountservice.event.CreateAccountEvent;
import faang.school.accountservice.exception.JsonMappingException;
import faang.school.accountservice.publisher.CreateAccountPublisher;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.request.RequestService;
import faang.school.accountservice.service.request_task.handler.RequestTaskHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendCreateAccountNotification implements RequestTaskHandler {

    private final ObjectMapper objectMapper;
    private final CreateAccountPublisher publisher;
    private final RequestService requestService;
    private final AccountRepository accountRepository;

    private final CheckAccountsQuantity checkAccountsQuantity;
    private final CreateAccount createAccount;
    private final CreateBalanceAndBalanceAudit balanceAudit;

    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public void execute(Request request) {
        try {
            Account account = mapAccount(request);
            Account accountWithOwner = accountRepository.findById(account.getId()).orElseThrow(() ->
                    new EntityNotFoundException("Account with id " + account.getId() + " not found"));

            CreateAccountEvent event = CreateAccountEvent.builder()
                    .ownerId(accountWithOwner.getOwner().getOwnerId())
                    .ownerType(accountWithOwner.getOwner().getOwnerType().name())
                    .accountType(account.getType().name())
                    .currency(account.getCurrency().name())
                    .build();

            request.setContext(null);
            request.setRequestStatus(RequestStatus.DONE);
            setRequestTaskStatus(request, RequestTaskStatus.DONE);

            requestService.updateRequest(request);
            publisher.publish(event);
            log.info("Finished processing request task with type: {}",
                    RequestTaskType.SEND_CREATE_ACCOUNT_NOTIFICATION);
            log.info("Successfully opened account with number: {}", account.getAccountNumber());

        } catch (OptimisticLockingFailureException e) {
            log.error("Optimistic locking failed after 3 retries for request with id: {}. " +
                    "Executing rollback.", request.getIdempotentToken(), e);
            rollback(request);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred during execution request with id: {}. " +
                    "Executing rollback.", request.getIdempotentToken(), e);
            rollback(request);
        }
    }

    @Override
    public long getHandlerId() {
        return 5;
    }

    @Override
    public void rollback(Request request) {
        request.setContext(null);
        request.setRequestStatus(RequestStatus.AWAITING);
        setRequestTaskStatus(request, RequestTaskStatus.AWAITING);
        requestService.updateRequest(request);

        List<RequestTask> tasks = request.getRequestTasks().stream()
                .filter(requestTask -> requestTask.getStatus() == RequestTaskStatus.DONE).toList();
        if (!tasks.isEmpty()) {
            balanceAudit.rollback(request);
            createAccount.rollback(request);
            checkAccountsQuantity.rollback(request);
        }
        log.info("Request task with type: {} rollback",RequestTaskType.SEND_CREATE_ACCOUNT_NOTIFICATION);
    }

    private void setRequestTaskStatus(Request request, RequestTaskStatus status) {
        request.getRequestTasks().stream()
                .filter(requestTask -> requestTask.getHandler().
                        equals(RequestTaskType.SEND_CREATE_ACCOUNT_NOTIFICATION))
                .forEach(requestTask -> requestTask.setStatus(status));
    }

    private Account mapAccount(Request request) {
        Account account;
        try {
            account = objectMapper.readValue(request.getContext(), Account.class);
        } catch (JsonProcessingException e) {
            throw new JsonMappingException(e.getMessage());
        }
        return account;
    }
}
