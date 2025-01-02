package faang.school.accountservice.service.request_task.handler.impl.create_account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.AccountRequest;
import faang.school.accountservice.entity.AccountOwner;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.enums.request_task.RequestTaskStatus;
import faang.school.accountservice.enums.request_task.RequestTaskType;
import faang.school.accountservice.exception.JsonMappingException;
import faang.school.accountservice.service.AccountOwnerService;
import faang.school.accountservice.service.request.RequestService;
import faang.school.accountservice.service.request_task.handler.RequestTaskHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckAccountsQuantity implements RequestTaskHandler {

    private final ObjectMapper objectMapper;
    private final AccountOwnerService accountOwnerService;
    private final RequestService requestService;

    @Value("${account.max-accounts-quantity}")
    private int maxAccountsQuantity;

    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public void execute(Request request) {
        try {
            AccountRequest accountRequest = mapAccountRequest(request);
            log.info("Start opening a new account for ownerId: {}, ownerType: {}",
                    accountRequest.getOwnerId(), accountRequest.getOwnerType());
            AccountOwner owner = accountOwnerService.
                    findOwner(accountRequest.getOwnerId(), accountRequest.getOwnerType());

            if (owner.getAccounts().size() >= maxAccountsQuantity) {
                setRequestStatus(request, RequestStatus.DONE);
                request.getRequestTasks().forEach(requestTask ->
                        requestTask.setStatus(RequestTaskStatus.DONE));
                requestService.updateRequest(request);

                throw new IllegalStateException("Forbidden have more than " +
                        maxAccountsQuantity + " accounts");
            }
            setRequestStatus(request, RequestStatus.PROCESSING);
            setRequestTaskStatus(request, RequestTaskStatus.DONE);
            requestService.updateRequest(request);
            log.info("Finished processing request task with type: {}, for request with id: {} ",
                    RequestTaskType.CHECK_ACCOUNTS_QUANTITY, request.getIdempotentToken());

        } catch (OptimisticLockingFailureException e) {
            log.error("Optimistic locking failed after 3 retries for request with id: {}. " +
                    "Executing rollback.", request.getIdempotentToken(), e);
            rollback(request);
            throw e;
        }
    }

    @Override
    public long getHandlerId() {
        return 1;
    }

    @Transactional
    @Override
    public void rollback(Request request) {
        setRequestStatus(request, RequestStatus.AWAITING);
        setRequestTaskStatus(request, RequestTaskStatus.AWAITING);
        requestService.updateRequest(request);
        log.info("Request task with type: {}, id: {} rollback",
                RequestTaskType.CHECK_ACCOUNTS_QUANTITY, request.getIdempotentToken());
    }

    private void setRequestStatus(Request request, RequestStatus requestStatus) {
        request.setRequestStatus(requestStatus);
    }

    private void setRequestTaskStatus(Request request, RequestTaskStatus requestTaskStatus) {
        request.getRequestTasks().stream()
                .filter(requestTask -> requestTask.getHandler().
                        equals(RequestTaskType.CHECK_ACCOUNTS_QUANTITY))
                .forEach(requestTask -> requestTask.setStatus(requestTaskStatus));
    }

    private AccountRequest mapAccountRequest(Request request) {
        AccountRequest accountRequest;
        try {
            accountRequest = objectMapper.readValue(request.getContext(), AccountRequest.class);
        } catch (JsonProcessingException e) {
            throw new JsonMappingException(e.getMessage());
        }
        return accountRequest;
    }
}
